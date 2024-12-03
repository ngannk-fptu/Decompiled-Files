/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.operations.QueueOperation;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;
import com.hazelcast.spi.ReadonlyOperation;

public class IsEmptyOperation
extends QueueOperation
implements ReadonlyOperation {
    public IsEmptyOperation() {
    }

    public IsEmptyOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        QueueContainer queueContainer = this.getContainer();
        this.response = queueContainer.size() == 0;
    }

    @Override
    public void afterRun() throws Exception {
        LocalQueueStatsImpl stats = this.getQueueService().getLocalQueueStatsImpl(this.name);
        stats.incrementOtherOperations();
    }

    @Override
    public int getId() {
        return 40;
    }
}

