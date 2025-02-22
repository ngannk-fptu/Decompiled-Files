/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl;

import com.hazelcast.replicatedmap.impl.operation.EvictionOperation;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.scheduler.EntryTaskScheduler;
import com.hazelcast.util.scheduler.ScheduledEntry;
import com.hazelcast.util.scheduler.ScheduledEntryProcessor;
import java.util.Collection;

public class ReplicatedMapEvictionProcessor
implements ScheduledEntryProcessor<Object, Object> {
    private ReplicatedRecordStore store;
    private NodeEngine nodeEngine;
    private int partitionId;

    public ReplicatedMapEvictionProcessor(ReplicatedRecordStore store, NodeEngine nodeEngine, int partitionId) {
        this.store = store;
        this.nodeEngine = nodeEngine;
        this.partitionId = partitionId;
    }

    @Override
    public void process(EntryTaskScheduler<Object, Object> scheduler, Collection<ScheduledEntry<Object, Object>> entries) {
        EvictionOperation evictionOperation = new EvictionOperation(this.store, entries);
        evictionOperation.setPartitionId(this.partitionId);
        this.nodeEngine.getOperationService().invokeOnTarget("hz:impl:replicatedMapService", evictionOperation, this.nodeEngine.getThisAddress());
    }
}

