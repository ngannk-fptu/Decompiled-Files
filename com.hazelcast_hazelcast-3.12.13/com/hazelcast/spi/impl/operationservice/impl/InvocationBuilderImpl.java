/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.impl.Invocation;
import com.hazelcast.spi.impl.operationservice.impl.InvocationFuture;
import com.hazelcast.spi.impl.operationservice.impl.PartitionInvocation;
import com.hazelcast.spi.impl.operationservice.impl.TargetInvocation;

class InvocationBuilderImpl
extends InvocationBuilder {
    private final Invocation.Context context;

    InvocationBuilderImpl(Invocation.Context context, String serviceName, Operation op, int partitionId) {
        this(context, serviceName, op, partitionId, null);
    }

    InvocationBuilderImpl(Invocation.Context context, String serviceName, Operation op, Address target) {
        this(context, serviceName, op, -1, target);
    }

    private InvocationBuilderImpl(Invocation.Context context, String serviceName, Operation op, int partitionId, Address target) {
        super(serviceName, op, partitionId, target);
        this.context = context;
    }

    public InternalCompletableFuture invoke() {
        Invocation invocation;
        this.op.setServiceName(this.serviceName);
        if (this.target == null) {
            this.op.setPartitionId(this.partitionId).setReplicaIndex(this.replicaIndex);
            invocation = new PartitionInvocation(this.context, this.op, this.doneCallback, this.tryCount, this.tryPauseMillis, this.callTimeout, this.resultDeserialized, this.failOnIndeterminateOperationState, this.endpointManager);
        } else {
            invocation = new TargetInvocation(this.context, this.op, this.target, this.doneCallback, this.tryCount, this.tryPauseMillis, this.callTimeout, this.resultDeserialized, this.endpointManager);
        }
        InvocationFuture future = invocation.invoke();
        if (this.executionCallback != null) {
            future.andThen(this.executionCallback);
        }
        return future;
    }
}

