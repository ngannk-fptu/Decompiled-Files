/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.executor.impl;

import com.hazelcast.executor.impl.operations.CancellationOperation;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.executor.DelegatingFuture;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;

final class CancellableDelegatingFuture<V>
extends DelegatingFuture<V> {
    public static final int CANCEL_TRY_COUNT = 50;
    public static final int CANCEL_TRY_PAUSE_MILLIS = 250;
    private final NodeEngine nodeEngine;
    private final String uuid;
    private final int partitionId;
    private final Address target;

    CancellableDelegatingFuture(InternalCompletableFuture future, NodeEngine nodeEngine, String uuid, int partitionId) {
        super(future, nodeEngine.getSerializationService());
        this.nodeEngine = nodeEngine;
        this.uuid = uuid;
        this.partitionId = partitionId;
        this.target = null;
    }

    CancellableDelegatingFuture(InternalCompletableFuture future, NodeEngine nodeEngine, String uuid, Address target) {
        super(future, nodeEngine.getSerializationService());
        this.nodeEngine = nodeEngine;
        this.uuid = uuid;
        this.target = target;
        this.partitionId = -1;
    }

    CancellableDelegatingFuture(InternalCompletableFuture future, V defaultValue, NodeEngine nodeEngine, String uuid, int partitionId) {
        super(future, nodeEngine.getSerializationService(), defaultValue);
        this.nodeEngine = nodeEngine;
        this.uuid = uuid;
        this.partitionId = partitionId;
        this.target = null;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (this.isDone()) {
            return false;
        }
        Future<Boolean> f = this.invokeCancelOperation(mayInterruptIfRunning);
        boolean cancelSuccessful = false;
        try {
            cancelSuccessful = f.get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
        this.complete(new CancellationException());
        return cancelSuccessful;
    }

    private Future<Boolean> invokeCancelOperation(boolean mayInterruptIfRunning) {
        CancellationOperation op = new CancellationOperation(this.uuid, mayInterruptIfRunning);
        OperationService opService = this.nodeEngine.getOperationService();
        InvocationBuilder builder = this.partitionId > -1 ? opService.createInvocationBuilder("hz:impl:executorService", (Operation)op, this.partitionId) : opService.createInvocationBuilder("hz:impl:executorService", (Operation)op, this.target);
        builder.setTryCount(50).setTryPauseMillis(250L);
        return builder.invoke();
    }
}

