import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemoSeekAssign {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ConsumerDemoSeekAssign.class.getName());

        String bootstrapServer = "localhost:9092";
        String topic = "first_topic";

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName() );
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName() );
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        TopicPartition tp = new TopicPartition(topic, 1);
        consumer.assign(Arrays.asList(tp));

        long offsetToReadFrom = 4L;
//        consumer.seek(tp, offsetToReadFrom);

        int leftToRead = 2;
        boolean keepOnReading = true;
        while(keepOnReading) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            logger.info("Retrieved record count: " + records.count());
            for( ConsumerRecord<String,String> record : records ) {
                logger.info("Key: " + record.key() + " Value: " + record.value() );
                logger.info("Partition: " + record.partition() + " Offset: " + record.offset() );

                if( --leftToRead == 0 ) {
                    keepOnReading = false;
                    break;
                }
            }
        }
    }
}
