/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.topic.impl;

import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.topic.impl.PublishOperation;
import com.hazelcast.topic.impl.TopicProxy;
import com.hazelcast.topic.impl.TopicService;

public class TotalOrderedTopicProxy<E>
extends TopicProxy<E> {
    private final int partitionId;

    public TotalOrderedTopicProxy(String name, NodeEngine nodeEngine, TopicService service) {
        super(name, nodeEngine, service);
        this.partitionId = nodeEngine.getPartitionService().getPartitionId(this.getNameAsPartitionAwareData());
    }

    @Override
    public void publish(E message) {
        Operation operation = new PublishOperation(this.getName(), this.toData(message)).setPartitionId(this.partitionId);
        InternalCompletableFuture f = this.invokeOnPartition(operation);
        f.join();
    }
}

