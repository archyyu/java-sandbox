package com.script.kafka;

import java.util.stream.IntStream;

//SOURCES Kafka.java
//SOURCES KafkaConsumer.java
//SOURCES Partition.java
//SOURCES Topic.java
//SOURCES Message.java

public class KafkaTest {
    

    public static void main(String[] args) {

        String topicName = "topicName";
        String consumer1 = "consumer1";
        String consumer2 = "consumer2";
        String consumer3 = "consumer3";

        Kafka kafka = new Kafka();

        IntStream.range(0, 100).forEach(
            i -> {

                Message message = new Message();
                message.index = i;
                message.name = "index:" + i;
                
                kafka.produce(message, topicName);
            }
        );

        IntStream.range(0, 50).forEach(
            i -> {
                Message message = (Message)kafka.consume(consumer1, topicName);
                if (message != null) {
                    System.err.println("consumer1:" + message.name);
                }


                message = (Message)kafka.consume(consumer2, topicName);
                if (message != null) {
                    System.err.println("consumer2:" + message.name);
                }

                message = (Message)kafka.consume(consumer3, topicName);
                if (message != null) {
                    System.err.println("consumer3:" + message.name);
                }

            }
        );


    }

}
