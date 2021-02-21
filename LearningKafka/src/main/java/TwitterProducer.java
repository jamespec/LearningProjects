import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwitterProducer {
    private final Logger logger = LoggerFactory.getLogger( TwitterProducer.class.getName());

    private final String consumerKey = "qfNOO5sfsiSa1kd8GSXWZcEg6";
    private final String consumerSecret = "TwW5FuvTVxoDZPgX869m7BTbJJ4eVctBIpGfGC8MhLfJ57Uh6Z";
    private final String token = "1356656219495280643-esva9tpj9R8rWsJil9VqlXdtOBTPOo";
    private final String secret = "bLY9PFSFHNqjpqVhT83yNzQfVvDLciKLuZTLYgRuJ3QJO";

    private final String kafkaBootstrapHost = "mac-mini.local:9092";
    private final String kafkaTopic = "twitter_tweets";

    private final List<String> terms = Lists.newArrayList("SP500");

    public TwitterProducer() {
    }

    public static void main(String[] args) {
        new TwitterProducer().run();
    }

    public void run()
    {
        logger.info("Setup to run");
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>(100000);

        Client client = createTwitterClient(msgQueue);
        client.connect();
        KafkaProducer<String, String> kafkaProducer = createKafkaProducer(kafkaBootstrapHost);

        Runtime.getRuntime().addShutdownHook( new Thread( () -> {
            logger.info("Application shutting down...");
            logger.info("Twitter client stop...");
            client.stop();
            logger.info("Kafka Producer close...");
            kafkaProducer.close();
            logger.info("Shutdown complete.");
        }) );

        // on a different thread, or multiple different threads....
        while (!client.isDone()) {
            String msg = null;
            try {
                msg = msgQueue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.stop();
            }
            if( msg != null ) {
                logger.info("Msg: " + msg);
                ProducerRecord<String, String> record = new ProducerRecord<>(kafkaTopic, null, msg);
                kafkaProducer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if (e != null) {
                            logger.error("Error Producing: ", e);
                        }
                    }
                });
            }

        }
        logger.info("Application exiting");
    }

    public Client createTwitterClient(BlockingQueue<String> msgQueue)
    {
        /*  Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        hosebirdEndpoint.trackTerms(terms);

        // These secrets should be read from a config file
        Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);

        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")                              // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        Client hosebirdClient = builder.build();
        return hosebirdClient;
    }

    public KafkaProducer<String, String> createKafkaProducer( String bootstrapHost )
    {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapHost);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName() );
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName() );

        // Additional Safe parameters - possibly impacting performance:
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
        properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");  // Happens to be default

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        return producer;
    }
}
