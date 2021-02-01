import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerDemoThread {
    public static void main(String[] args) {
        new ConsumerDemoThread().run();
    }

    private ConsumerDemoThread() {
    }

    private void run() {
        Logger logger = LoggerFactory.getLogger(ConsumerDemoThread.class.getName());

        CountDownLatch latch = new CountDownLatch(1);
        String bootstrapServer = "localhost:9092";
        String topic = "first_topic";
        String groupId = "thread_consumer2";

        ConsumerRunnable ct = new ConsumerRunnable(bootstrapServer, topic, groupId, latch);
        Thread thread = new Thread(ct);
        logger.info("Starting thread");
        thread.start();

        Runtime.getRuntime().addShutdownHook( new Thread( () -> {
            logger.info("Caught in shutdown hook");
            ct.shutdown();

            // Not sure why this is needed here when we have it next in the flow...
            try {
                latch.await();
            } catch( InterruptedException e ) {}
        }) );

        try {
            latch.await();
        } catch( InterruptedException e ) {}

        logger.info("Application shutting down");
    }

    class ConsumerRunnable implements Runnable
    {
        private Logger logger = LoggerFactory.getLogger(ConsumerDemoThread.class.getName());

        private String bootstrapServer;
        private String topic;
        private String groupId;
        private CountDownLatch latch;
        private KafkaConsumer<String, String> consumer;

        ConsumerRunnable(String bootstrapServer, String topic, String groupId, CountDownLatch latch ) {
            this.bootstrapServer = bootstrapServer;
            this.topic = topic;
            this.groupId = groupId;
            this.latch = latch;
        }

        @Override
        public void run() {
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName() );
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName() );
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId );
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            consumer = new KafkaConsumer<>(properties);
            consumer.subscribe(Arrays.asList(topic));

            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        logger.info("Key: " + record.key() + " Value: " + record.value());
                        logger.info("Partition: " + record.partition() + " Offset: " + record.offset());
                    }
                }
            } catch( WakeupException e ) {
                logger.info("Consumer terminating");
            } finally {
                consumer.close();
                latch.countDown();
            }
        }

        public void shutdown() {
            consumer.wakeup();
        }
    }
}
