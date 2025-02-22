/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.concurrent.lock.operations.GetLockCountOperation;
import com.hazelcast.concurrent.lock.operations.GetRemainingLeaseTimeOperation;
import com.hazelcast.concurrent.lock.operations.IsLockedOperation;
import com.hazelcast.concurrent.lock.operations.LockOperation;
import com.hazelcast.concurrent.lock.operations.UnlockOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.ThreadUtil;
import java.util.concurrent.TimeUnit;

public final class LockProxySupport {
    private final ObjectNamespace namespace;
    private final long maxLeaseTimeInMillis;

    public LockProxySupport(ObjectNamespace namespace, long maxLeaseTimeInMillis) {
        this.namespace = namespace;
        this.maxLeaseTimeInMillis = maxLeaseTimeInMillis;
    }

    public boolean isLocked(NodeEngine nodeEngine, Data key) {
        IsLockedOperation operation = new IsLockedOperation(this.namespace, key);
        InternalCompletableFuture f = this.invoke(nodeEngine, operation, key);
        return (Boolean)f.join();
    }

    private InternalCompletableFuture invoke(NodeEngine nodeEngine, Operation operation, Data key) {
        int partitionId = nodeEngine.getPartitionService().getPartitionId(key);
        return nodeEngine.getOperationService().invokeOnPartition("hz:impl:lockService", operation, partitionId);
    }

    public boolean isLockedByCurrentThread(NodeEngine nodeEngine, Data key) {
        IsLockedOperation operation = new IsLockedOperation(this.namespace, key, ThreadUtil.getThreadId());
        InternalCompletableFuture f = this.invoke(nodeEngine, operation, key);
        return (Boolean)f.join();
    }

    public int getLockCount(NodeEngine nodeEngine, Data key) {
        GetLockCountOperation operation = new GetLockCountOperation(this.namespace, key);
        InternalCompletableFuture f = this.invoke(nodeEngine, operation, key);
        return ((Number)f.join()).intValue();
    }

    public long getRemainingLeaseTime(NodeEngine nodeEngine, Data key) {
        GetRemainingLeaseTimeOperation operation = new GetRemainingLeaseTimeOperation(this.namespace, key);
        InternalCompletableFuture f = this.invoke(nodeEngine, operation, key);
        return ((Number)f.join()).longValue();
    }

    public void lock(NodeEngine nodeEngine, Data key) {
        this.lock(nodeEngine, key, -1L);
    }

    public void lock(NodeEngine nodeEngine, Data key, long leaseTime) {
        leaseTime = this.getLeaseTime(leaseTime);
        LockOperation operation = new LockOperation(this.namespace, key, ThreadUtil.getThreadId(), leaseTime, -1L);
        InternalCompletableFuture f = this.invoke(nodeEngine, operation, key);
        if (!((Boolean)f.join()).booleanValue()) {
            throw new IllegalStateException();
        }
    }

    public void lockInterruptly(NodeEngine nodeEngine, Data key) throws InterruptedException {
        this.lockInterruptly(nodeEngine, key, -1L);
    }

    public void lockInterruptly(NodeEngine nodeEngine, Data key, long leaseTime) throws InterruptedException {
        leaseTime = this.getLeaseTime(leaseTime);
        LockOperation operation = new LockOperation(this.namespace, key, ThreadUtil.getThreadId(), leaseTime, -1L);
        InternalCompletableFuture f = this.invoke(nodeEngine, operation, key);
        try {
            f.get();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrowAllowInterrupted(t);
        }
    }

    private long getLeaseTime(long leaseTime) {
        if (leaseTime > this.maxLeaseTimeInMillis) {
            throw new IllegalArgumentException("Max allowed lease time: " + this.maxLeaseTimeInMillis + "ms. Given lease time: " + leaseTime + "ms.");
        }
        if (leaseTime < 0L) {
            leaseTime = this.maxLeaseTimeInMillis;
        }
        return leaseTime;
    }

    public boolean tryLock(NodeEngine nodeEngine, Data key) {
        try {
            return this.tryLock(nodeEngine, key, 0L, TimeUnit.MILLISECONDS, -1L, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public boolean tryLock(NodeEngine nodeEngine, Data key, long timeout, TimeUnit timeunit) throws InterruptedException {
        return this.tryLock(nodeEngine, key, timeout, timeunit, -1L, TimeUnit.MILLISECONDS);
    }

    public boolean tryLock(NodeEngine nodeEngine, Data key, long timeout, TimeUnit timeunit, long leaseTime, TimeUnit leaseTimeunit) throws InterruptedException {
        long timeoutInMillis = this.getTimeInMillis(timeout, timeunit);
        long leaseTimeInMillis = this.getTimeInMillis(leaseTime, leaseTimeunit);
        LockOperation operation = new LockOperation(this.namespace, key, ThreadUtil.getThreadId(), leaseTimeInMillis, timeoutInMillis);
        InternalCompletableFuture f = this.invoke(nodeEngine, operation, key);
        try {
            return (Boolean)f.get();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrowAllowInterrupted(t);
        }
    }

    private long getTimeInMillis(long time, TimeUnit timeunit) {
        return timeunit != null ? timeunit.toMillis(time) : time;
    }

    public void unlock(NodeEngine nodeEngine, Data key) {
        UnlockOperation operation = new UnlockOperation(this.namespace, key, ThreadUtil.getThreadId());
        InternalCompletableFuture f = this.invoke(nodeEngine, operation, key);
        f.join();
    }

    public void forceUnlock(NodeEngine nodeEngine, Data key) {
        UnlockOperation operation = new UnlockOperation(this.namespace, key, -1L, true);
        InternalCompletableFuture f = this.invoke(nodeEngine, operation, key);
        f.join();
    }

    public ObjectNamespace getNamespace() {
        return this.namespace;
    }
}

