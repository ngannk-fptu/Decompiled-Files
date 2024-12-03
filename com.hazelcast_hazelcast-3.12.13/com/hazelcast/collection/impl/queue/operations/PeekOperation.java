/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.QueueDataSerializerHook;
import com.hazelcast.collection.impl.queue.QueueItem;
import com.hazelcast.collection.impl.queue.operations.QueueOperation;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.ReadonlyOperation;

public final class PeekOperation
extends QueueOperation
implements IdentifiedDataSerializable,
ReadonlyOperation {
    public PeekOperation() {
    }

    public PeekOperation(String name) {
        super(name);
    }

    @Override
    public void run() {
        QueueContainer queueContainer = this.getContainer();
        QueueItem item = queueContainer.peek();
        this.response = item != null ? item.getData() : null;
    }

    @Override
    public void afterRun() throws Exception {
        LocalQueueStatsImpl stats = this.getQueueService().getLocalQueueStatsImpl(this.name);
        stats.incrementOtherOperations();
    }

    @Override
    public int getFactoryId() {
        return QueueDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }
}

