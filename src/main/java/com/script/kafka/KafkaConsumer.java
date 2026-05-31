package com.script.kafka;

import java.util.concurrent.atomic.AtomicInteger;

public class KafkaConsumer {
    
    private String name;

    private long lastActiveTime;

    private AtomicInteger index = new AtomicInteger(0);

    public KafkaConsumer(String name, long timestamp) {
        this.name = name;
        this.lastActiveTime = timestamp;
    }

    public int getReadIndex() {
        return this.index.addAndGet(1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    

}
