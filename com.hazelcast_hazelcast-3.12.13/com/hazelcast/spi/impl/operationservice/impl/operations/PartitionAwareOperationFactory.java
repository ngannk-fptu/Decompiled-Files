/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl.operations;

import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;

public abstract class PartitionAwareOperationFactory
implements OperationFactory {
    protected int[] partitions;

    public PartitionAwareOperationFactory createFactoryOnRunner(NodeEngine nodeEngine, int[] partitions) {
        return this;
    }

    public abstract Operation createPartitionOperation(int var1);

    @Override
    public Operation createOperation() {
        throw new UnsupportedOperationException("Use createPartitionOperation() with PartitionAwareOperationFactory");
    }
}

