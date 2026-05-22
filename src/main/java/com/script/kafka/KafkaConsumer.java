package com.script.kafka;

public class KafkaConsumer {
    
    private String name;

    private long lastActiveTime;

    public KafkaConsumer(String name, long timestamp) {
        this.name = name;
        this.lastActiveTime = timestamp;
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
