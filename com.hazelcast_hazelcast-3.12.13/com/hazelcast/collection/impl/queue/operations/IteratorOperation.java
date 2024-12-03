/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueOperation;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.impl.SerializableList;
import java.util.List;

public class IteratorOperation
extends QueueOperation
implements ReadonlyOperation {
    public IteratorOperation() {
    }

    public IteratorOperation(String name) {
        super(name);
    }

    @Override
    public void run() {
        QueueContainer queueContainer = this.getContainer();
        List<Data> dataList = queueContainer.getAsDataList();
        this.response = new SerializableList(dataList);
    }

    @Override
    public void afterRun() throws Exception {
        LocalQueueStatsImpl stats = this.getQueueService().getLocalQueueStatsImpl(this.name);
        stats.incrementOtherOperations();
    }

    @Override
    public int getId() {
        return 14;
    }
}

