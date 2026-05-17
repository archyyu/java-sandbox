package src.main.java.com.script.kafka;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Kafka {

    private final int defaultPartitionSize = 20;
    
    private Map<String, Topic> topicMessagebus = new ConcurrentHashMap<>();

    private Map<String, KafkaConsumer> consumerMap = new ConcurrentHashMap<>();
    
    public void evictTheIdleConsumers() {

    }

    public Object consume(String consumer, String topicName) {
        if (consumer == null || consumer.equals("")) {
            return null;
        }
        KafkaConsumer kafkaConsumer = consumerMap.computeIfAbsent(consumer, f -> new KafkaConsumer(consumer, System.currentTimeMillis()));
        kafkaConsumer.setLastActiveTime(System.currentTimeMillis());

        Topic topic = this.topicMessagebus.computeIfAbsent(topicName, f -> new Topic(defaultPartitionSize));
        topic.consumeMessage();

        

        return null;
    }

    public void produce(Object object) {

    }

}
