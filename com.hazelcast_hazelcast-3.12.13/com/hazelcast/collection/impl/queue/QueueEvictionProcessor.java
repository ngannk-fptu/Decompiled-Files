/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue;

import com.hazelcast.collection.impl.queue.operations.CheckAndEvictOperation;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.scheduler.EntryTaskScheduler;
import com.hazelcast.util.scheduler.ScheduledEntry;
import com.hazelcast.util.scheduler.ScheduledEntryProcessor;
import java.util.Collection;

public class QueueEvictionProcessor
implements ScheduledEntryProcessor<String, Void> {
    private final NodeEngine nodeEngine;

    public QueueEvictionProcessor(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
    }

    @Override
    public void process(EntryTaskScheduler<String, Void> scheduler, Collection<ScheduledEntry<String, Void>> entries) {
        if (entries.isEmpty()) {
            return;
        }
        IPartitionService partitionService = this.nodeEngine.getPartitionService();
        OperationService operationService = this.nodeEngine.getOperationService();
        for (ScheduledEntry<String, Void> entry : entries) {
            String name = entry.getKey();
            int partitionId = partitionService.getPartitionId(this.nodeEngine.toData(name));
            Operation op = new CheckAndEvictOperation(entry.getKey()).setPartitionId(partitionId);
            operationService.invokeOnPartition(op).join();
        }
    }
}

