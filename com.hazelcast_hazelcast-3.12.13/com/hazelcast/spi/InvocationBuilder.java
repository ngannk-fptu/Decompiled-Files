/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.Preconditions;

public abstract class InvocationBuilder {
    public static final long DEFAULT_CALL_TIMEOUT = -1L;
    public static final int DEFAULT_REPLICA_INDEX = 0;
    public static final int DEFAULT_TRY_COUNT = 250;
    public static final long DEFAULT_TRY_PAUSE_MILLIS = 500L;
    public static final boolean DEFAULT_DESERIALIZE_RESULT = true;
    protected final String serviceName;
    protected final Operation op;
    protected final int partitionId;
    protected final Address target;
    protected ExecutionCallback<Object> executionCallback;
    protected Runnable doneCallback;
    protected long callTimeout = -1L;
    protected int replicaIndex;
    protected int tryCount = 250;
    protected long tryPauseMillis = 500L;
    protected boolean resultDeserialized = true;
    protected boolean failOnIndeterminateOperationState;
    protected EndpointManager endpointManager;

    protected InvocationBuilder(String serviceName, Operation op, int partitionId, Address target) {
        this.serviceName = serviceName;
        this.op = op;
        this.partitionId = partitionId;
        this.target = target;
    }

    public InvocationBuilder setReplicaIndex(int replicaIndex) {
        if (replicaIndex < 0 || replicaIndex >= 7) {
            throw new IllegalArgumentException("Replica index is out of range [0-6]");
        }
        this.replicaIndex = replicaIndex;
        return this;
    }

    public boolean isResultDeserialized() {
        return this.resultDeserialized;
    }

    public InvocationBuilder setResultDeserialized(boolean resultDeserialized) {
        this.resultDeserialized = resultDeserialized;
        return this;
    }

    public InvocationBuilder setTryCount(int tryCount) {
        this.tryCount = tryCount;
        return this;
    }

    public boolean shouldFailOnIndeterminateOperationState() {
        return this.failOnIndeterminateOperationState;
    }

    public InvocationBuilder setFailOnIndeterminateOperationState(boolean failOnIndeterminateOperationState) {
        Preconditions.checkFalse(failOnIndeterminateOperationState && this.partitionId == -1, "failOnIndeterminateOperationState can be used with only partition invocations");
        this.failOnIndeterminateOperationState = failOnIndeterminateOperationState;
        return this;
    }

    public InvocationBuilder setTryPauseMillis(long tryPauseMillis) {
        this.tryPauseMillis = tryPauseMillis;
        return this;
    }

    public InvocationBuilder setCallTimeout(long callTimeout) {
        this.callTimeout = callTimeout;
        return this;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public Operation getOp() {
        return this.op;
    }

    public int getReplicaIndex() {
        return this.replicaIndex;
    }

    public int getTryCount() {
        return this.tryCount;
    }

    public long getTryPauseMillis() {
        return this.tryPauseMillis;
    }

    public Address getTarget() {
        return this.target;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public long getCallTimeout() {
        return this.callTimeout;
    }

    public ExecutionCallback<Object> getExecutionCallback() {
        return this.executionCallback;
    }

    public InvocationBuilder setExecutionCallback(ExecutionCallback<Object> executionCallback) {
        this.executionCallback = executionCallback;
        return this;
    }

    public InvocationBuilder setEndpointManager(EndpointManager endpointManager) {
        this.endpointManager = endpointManager;
        return this;
    }

    protected ExecutionCallback getTargetExecutionCallback() {
        return this.executionCallback;
    }

    public InvocationBuilder setDoneCallback(Runnable doneCallback) {
        this.doneCallback = doneCallback;
        return this;
    }

    public abstract <E> InternalCompletableFuture<E> invoke();
}

