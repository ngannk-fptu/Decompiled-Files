/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationexecutor.impl;

import com.hazelcast.spi.impl.operationexecutor.impl.OperationExecutorImpl;
import com.hazelcast.spi.impl.operationservice.PartitionTaskFactory;
import java.util.BitSet;

public class TaskBatch {
    private final PartitionTaskFactory taskFactory;
    private final BitSet partitions;
    private final int threadId;
    private final int partitionThreadCount;
    private int nextPartitionId;

    public TaskBatch(PartitionTaskFactory taskFactory, BitSet partitions, int threadId, int partitionThreadCount) {
        this.taskFactory = taskFactory;
        this.partitions = partitions;
        this.threadId = threadId;
        this.partitionThreadCount = partitionThreadCount;
    }

    public PartitionTaskFactory taskFactory() {
        return this.taskFactory;
    }

    public Object next() {
        int partitionId = this.nextPartitionId();
        return partitionId == -1 ? null : this.taskFactory.create(partitionId);
    }

    private int nextPartitionId() {
        int partitionId;
        do {
            if ((partitionId = this.partitions.nextSetBit(this.nextPartitionId)) == -1) {
                return -1;
            }
            this.nextPartitionId = partitionId + 1;
        } while (OperationExecutorImpl.getPartitionThreadId(partitionId, this.partitionThreadCount) != this.threadId);
        return partitionId;
    }
}

