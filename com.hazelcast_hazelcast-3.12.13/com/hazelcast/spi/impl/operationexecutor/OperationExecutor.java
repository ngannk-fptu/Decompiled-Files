/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationexecutor;

import com.hazelcast.nio.Packet;
import com.hazelcast.spi.LiveOperationsTracker;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.PartitionSpecificRunnable;
import com.hazelcast.spi.impl.operationexecutor.OperationRunner;
import com.hazelcast.spi.impl.operationservice.PartitionTaskFactory;
import com.hazelcast.util.function.Consumer;
import java.util.BitSet;

public interface OperationExecutor
extends Consumer<Packet>,
LiveOperationsTracker {
    @Deprecated
    public int getRunningOperationCount();

    @Deprecated
    public int getQueueSize();

    @Deprecated
    public int getPriorityQueueSize();

    public long getExecutedOperationCount();

    public int getPartitionThreadCount();

    public int getGenericThreadCount();

    public OperationRunner[] getPartitionOperationRunners();

    public OperationRunner[] getGenericOperationRunners();

    public void execute(Operation var1);

    public void executeOnPartitions(PartitionTaskFactory var1, BitSet var2);

    public void execute(PartitionSpecificRunnable var1);

    public void executeOnPartitionThreads(Runnable var1);

    public void run(Operation var1);

    public void runOrExecute(Operation var1);

    public boolean isRunAllowed(Operation var1);

    public boolean isInvocationAllowed(Operation var1, boolean var2);

    public int getPartitionThreadId(int var1);

    public void start();

    public void shutdown();
}

