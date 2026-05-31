package com.script.kafka;

import java.util.List;
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
        
        int consumerSize = this.getAvaliableConsumers().size();

        int sizeEachConsumer = ((topic.getPartitionSize() / consumerSize) +
                 (topic.getPartitionSize()%consumerSize > 0 ? 1 : 0));

        int start = this.getConsumeIndex(consumer) * sizeEachConsumer;
        int end = Math.min(start + sizeEachConsumer, topic.getPartitionSize());

        int consumeIndex = (kafkaConsumer.getReadIndex() % (end - start)) + start;
        return topic.consumeMessage(consumeIndex);

    }

    public int getConsumeIndex(String consumer) {

        List<KafkaConsumer> list = this.getAvaliableConsumers();
        List<String> nameList = list.stream().map( item -> { return item.getName(); }).toList();

        return nameList.indexOf(consumer);
    }

    public List<KafkaConsumer> getAvaliableConsumers() {

        long avaliableTime = System.currentTimeMillis() - 50 * 1000;
        return this.consumerMap.values().stream().filter( item -> { return (item.getLastActiveTime() > avaliableTime); }).toList();

    }

    public void produce(Object object, String topicName) {

        Topic topic = this.topicMessagebus.computeIfAbsent(topicName, f -> new Topic(defaultPartitionSize));
        topic.appendMessage(object);

    }

}
