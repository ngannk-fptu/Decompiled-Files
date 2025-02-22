/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.spi.impl.operationexecutor.impl;

import com.hazelcast.instance.NodeExtension;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.impl.operationexecutor.OperationRunner;
import com.hazelcast.spi.impl.operationexecutor.impl.OperationQueue;
import com.hazelcast.spi.impl.operationexecutor.impl.OperationThread;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class PartitionOperationThread
extends OperationThread {
    private final OperationRunner[] partitionOperationRunners;

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public PartitionOperationThread(String name, int threadId, OperationQueue queue, ILogger logger, NodeExtension nodeExtension, OperationRunner[] partitionOperationRunners, ClassLoader configClassLoader) {
        super(name, threadId, queue, logger, nodeExtension, false, configClassLoader);
        this.partitionOperationRunners = partitionOperationRunners;
    }

    @Override
    public OperationRunner operationRunner(int partitionId) {
        return this.partitionOperationRunners[partitionId];
    }

    @Probe
    int priorityPendingCount() {
        return this.queue.prioritySize();
    }

    @Probe
    int normalPendingCount() {
        return this.queue.normalSize();
    }
}

