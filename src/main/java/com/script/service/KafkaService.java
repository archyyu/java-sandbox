package com.script.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.script.kafka.Kafka;

@Service
public class KafkaService {
    

    private final Kafka kafka;

    public KafkaService(@Qualifier("mainKafka") Kafka kafka) {
        this.kafka = kafka;
    }

    public void produceMessage(Object object, String topic) {
        this.kafka.produce(object, topic);
    }

    public Object consumeMessage(String consumer, String topic) {
        return this.kafka.consume(consumer, topic);
    }


}
