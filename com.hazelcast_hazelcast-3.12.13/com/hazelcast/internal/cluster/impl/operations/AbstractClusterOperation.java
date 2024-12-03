/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.internal.cluster.impl.operations.JoinOperation;
import com.hazelcast.spi.Operation;

abstract class AbstractClusterOperation
extends Operation
implements JoinOperation {
    AbstractClusterOperation() {
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    public final String getServiceName() {
        return "hz:core:clusterService";
    }

    @Override
    public int getFactoryId() {
        return 0;
    }
}

