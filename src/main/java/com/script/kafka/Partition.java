package com.script.kafka;

import java.io.FileWriter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Partition {
    
    private Queue<Object> messages = new ConcurrentLinkedDeque<Object>();

    public boolean appendMessage(Object object) {
        return this.messages.add(object);
    }

    public Object readMessage() {
        if (this.messages.isEmpty()) {
            return null;
        }
        return this.messages.poll();
    }
    
}
