/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationexecutor.impl;

import com.hazelcast.instance.NodeExtension;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.impl.operationexecutor.OperationRunner;
import com.hazelcast.spi.impl.operationexecutor.impl.OperationQueue;
import com.hazelcast.spi.impl.operationexecutor.impl.OperationThread;

public final class GenericOperationThread
extends OperationThread {
    private final OperationRunner operationRunner;

    public GenericOperationThread(String name, int threadId, OperationQueue queue, ILogger logger, NodeExtension nodeExtension, OperationRunner operationRunner, boolean priority, ClassLoader configClassLoader) {
        super(name, threadId, queue, logger, nodeExtension, priority, configClassLoader);
        this.operationRunner = operationRunner;
    }

    @Override
    public OperationRunner operationRunner(int partitionId) {
        return this.operationRunner;
    }
}

