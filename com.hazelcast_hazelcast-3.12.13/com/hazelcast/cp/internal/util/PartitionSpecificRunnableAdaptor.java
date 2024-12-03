/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.util;

import com.hazelcast.spi.impl.PartitionSpecificRunnable;

public class PartitionSpecificRunnableAdaptor
implements PartitionSpecificRunnable {
    private final Runnable task;
    private final int partitionId;

    public PartitionSpecificRunnableAdaptor(Runnable task, int partitionId) {
        this.task = task;
        this.partitionId = partitionId;
    }

    @Override
    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public void run() {
        this.task.run();
    }

    public String toString() {
        return "PartitionSpecificRunnableAdaptor{task=" + this.task + ", partitionId=" + this.partitionId + '}';
    }
}

