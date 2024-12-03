/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueOperation;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;
import com.hazelcast.spi.ReadonlyOperation;

public class SizeOperation
extends QueueOperation
implements ReadonlyOperation {
    public SizeOperation() {
    }

    public SizeOperation(String name) {
        super(name);
    }

    @Override
    public void run() {
        QueueContainer queueContainer = this.getContainer();
        this.response = queueContainer.size();
    }

    @Override
    public void afterRun() throws Exception {
        LocalQueueStatsImpl stats = this.getQueueService().getLocalQueueStatsImpl(this.name);
        stats.incrementOtherOperations();
    }

    @Override
    public int getId() {
        return 22;
    }
}

