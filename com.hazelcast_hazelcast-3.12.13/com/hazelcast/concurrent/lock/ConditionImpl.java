/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.concurrent.lock.LockProxy;
import com.hazelcast.concurrent.lock.operations.AwaitOperation;
import com.hazelcast.concurrent.lock.operations.BeforeAwaitOperation;
import com.hazelcast.concurrent.lock.operations.SignalOperation;
import com.hazelcast.core.ICondition;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.ThreadUtil;
import java.util.Date;
import java.util.concurrent.TimeUnit;

final class ConditionImpl
implements ICondition {
    private final LockProxy lockProxy;
    private final int partitionId;
    private final ObjectNamespace namespace;
    private final String conditionId;

    public ConditionImpl(LockProxy lockProxy, String id) {
        this.lockProxy = lockProxy;
        this.partitionId = lockProxy.getPartitionId();
        this.namespace = lockProxy.getNamespace();
        this.conditionId = id;
    }

    @Override
    public void await() throws InterruptedException {
        this.await(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    @Override
    public void awaitUninterruptibly() {
        try {
            this.await(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            ExceptionUtil.sneakyThrow(e);
        }
    }

    @Override
    public long awaitNanos(long nanosTimeout) throws InterruptedException {
        long start = System.nanoTime();
        this.await(nanosTimeout, TimeUnit.NANOSECONDS);
        long end = System.nanoTime();
        return nanosTimeout - (end - start);
    }

    @Override
    public boolean await(long time, TimeUnit unit) throws InterruptedException {
        long threadId = ThreadUtil.getThreadId();
        this.beforeAwait(threadId);
        return this.doAwait(time, unit, threadId);
    }

    private boolean doAwait(long time, TimeUnit unit, long threadId) throws InterruptedException {
        try {
            long timeout = unit.toMillis(time);
            Data key = this.lockProxy.getKeyData();
            AwaitOperation op = new AwaitOperation(this.namespace, key, threadId, timeout, this.conditionId);
            InternalCompletableFuture f = this.invoke(op);
            return Boolean.TRUE.equals(f.get());
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrowAllowInterrupted(t);
        }
    }

    private void beforeAwait(long threadId) {
        Data key = this.lockProxy.getKeyData();
        BeforeAwaitOperation op = new BeforeAwaitOperation(this.namespace, key, threadId, this.conditionId);
        InternalCompletableFuture f = this.invoke(op);
        f.join();
    }

    private InternalCompletableFuture invoke(Operation op) {
        NodeEngine nodeEngine = this.lockProxy.getNodeEngine();
        return nodeEngine.getOperationService().invokeOnPartition("hz:impl:lockService", op, this.partitionId);
    }

    @Override
    public boolean awaitUntil(Date deadline) throws InterruptedException {
        long until = deadline.getTime();
        long durationMs = until - Clock.currentTimeMillis();
        if (durationMs <= 0L) {
            return false;
        }
        return this.await(durationMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void signal() {
        this.signal(false);
    }

    private void signal(boolean all) {
        long threadId = ThreadUtil.getThreadId();
        Data key = this.lockProxy.getKeyData();
        SignalOperation op = new SignalOperation(this.namespace, key, threadId, this.conditionId, all);
        InternalCompletableFuture f = this.invoke(op);
        f.join();
    }

    @Override
    public void signalAll() {
        this.signal(true);
    }
}

