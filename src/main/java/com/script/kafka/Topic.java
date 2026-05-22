package com.script.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Topic {
    
    private List<Partition> list = new ArrayList<>();

    private int offset = 0;

    private AtomicInteger produceIndex;

    public Topic(int partitionSize) {
        for(int i=0;i<partitionSize;i++) {
            list.add(new Partition());
        }
        this.produceIndex = new AtomicInteger(0);
    }

    public Object consumeMessage(int consumerIndex) {
        return this.list.get(consumerIndex).readMessage();
    }

    private Partition getPartitionByIndex(int index) {
        return this.list.get(index % this.list.size());
    }

    public void appendMessage(Object object) {
        Partition partition = this.getPartitionByIndex(this.produceIndex.addAndGet(1));
        partition.appendMessage(object);
    }

    

}
