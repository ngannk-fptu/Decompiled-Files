/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationexecutor;

import com.hazelcast.nio.Packet;
import com.hazelcast.spi.Operation;

public abstract class OperationRunner {
    protected final int partitionId;
    protected volatile Object currentTask;
    private volatile Thread currentThread;

    public OperationRunner(int partitionId) {
        this.partitionId = partitionId;
    }

    public abstract long executedOperationsCount();

    public abstract void run(Packet var1) throws Exception;

    public abstract void run(Runnable var1);

    public abstract void run(Operation var1);

    public final Object currentTask() {
        return this.currentTask;
    }

    public final void setCurrentThread(Thread currentThread) {
        this.currentThread = currentThread;
    }

    public final Thread currentThread() {
        return this.currentThread;
    }

    public final int getPartitionId() {
        return this.partitionId;
    }
}

