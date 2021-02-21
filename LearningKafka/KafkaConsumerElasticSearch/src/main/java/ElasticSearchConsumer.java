import com.google.gson.JsonParser;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ElasticSearchConsumer
{
    RestHighLevelClient elasticSearchClient;
    KafkaConsumer<String, String> kafkaConsumer;

    public static RestHighLevelClient createClient()
    {
        String hostname = "mac-mini";
        String username = "";
        String password = "";

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));

        RestClientBuilder builder = RestClient.builder(
                new HttpHost(hostname, 9200, "http"))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                        return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });

        return new RestHighLevelClient(builder);
    }

    public static KafkaConsumer<String, String> createKafkaConsumer( String topic )
    {
        String bootstrapServer = "mac-mini.local:9092";
        String groupId = "kafka-demo-elasticsearch4";

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName() );
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName() );
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");  // We'll use manual commit.
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "5");       // Process only 5 records in a poll

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(topic));
        return consumer;
    }


    public static void main(String[] args) throws IOException {
        new ElasticSearchConsumer().run();
    }

    private void run()
    {
        Logger logger = LoggerFactory.getLogger(ElasticSearchConsumer.class.getName());
        CountDownLatch latch = new CountDownLatch(1);

        elasticSearchClient = createClient();
        kafkaConsumer = createKafkaConsumer("twitter_tweets");

        Runtime.getRuntime().addShutdownHook( new Thread( () -> {
            logger.info("Application shutting down...");
            shutdown();

            try {
                latch.await();
            } catch( InterruptedException e ) {}
        }) );

        try {
            while(true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(10000));

                int recordCount = records.count();
                logger.info("Received " + recordCount + " records.");

                if (recordCount > 0) {
                    BulkRequest bulkRequest = new BulkRequest();

                    for (ConsumerRecord<String, String> record : records) {
                        String id;
                        // Create reproducible ID so that the processing is idempotent.
                        // Option 1: Use Kafka message id
                        // id = record.topic() + "_" + record.partition() + "_" + record.offset();
                        // Or, read unique id from tweet
                        try {
                            id = extractIdFromTweet(record.value());
                            IndexRequest indexRequest = new IndexRequest("twitter")
                                    .id(id)
                                    .source(record.value(), XContentType.JSON);

                            bulkRequest.add(indexRequest);
                        } catch (NullPointerException e) {
                            logger.warn("Skipping bad data: " + record.value());
                        }
                    }
                    BulkResponse bulkItemResponses = elasticSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);

                    logger.info("Records processed about to commit...");
                    kafkaConsumer.commitSync();
                    logger.info("Offsets committed.");
                }
            }
        } catch( IOException e ) {
             e.printStackTrace();
        }
        catch(WakeupException e) {
            logger.info("Consumer terminating...");
        } finally {
            logger.info("Close Kafka Consumer...");
            kafkaConsumer.close();
            try {
                logger.info("Close ElasticSearch Client...");
                elasticSearchClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("Shutdown complete.");
        latch.countDown();
    }

    private static String extractIdFromTweet( String tweetJson )
    {
        return JsonParser.parseString(tweetJson).getAsJsonObject().get("id_str").getAsString();
    }

    private void shutdown() {
        kafkaConsumer.wakeup();
    }
}
