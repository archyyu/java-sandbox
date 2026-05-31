package com.script.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.script.kafka.Kafka;

@Configuration
public class KafkaConfig {

    @Bean
    @Qualifier("mainKafka")
    public Kafka mainKafka() {
        return new Kafka();
    }

}
