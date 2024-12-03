/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.concurrent.lock.ConditionImpl;
import com.hazelcast.concurrent.lock.InternalLockNamespace;
import com.hazelcast.concurrent.lock.LockProxySupport;
import com.hazelcast.concurrent.lock.LockServiceImpl;
import com.hazelcast.core.ICondition;
import com.hazelcast.core.ILock;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.util.Preconditions;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class LockProxy
extends AbstractDistributedObject<LockServiceImpl>
implements ILock {
    private final String name;
    private final LockProxySupport lockSupport;
    private final Data key;
    private final int partitionId;

    public LockProxy(NodeEngine nodeEngine, LockServiceImpl lockService, String name) {
        super(nodeEngine, lockService);
        this.name = name;
        this.key = this.getNameAsPartitionAwareData();
        this.lockSupport = new LockProxySupport(new InternalLockNamespace(name), lockService.getMaxLeaseTimeInMillis());
        this.partitionId = this.getNodeEngine().getPartitionService().getPartitionId(this.key);
    }

    @Override
    public boolean isLocked() {
        return this.lockSupport.isLocked(this.getNodeEngine(), this.key);
    }

    @Override
    public boolean isLockedByCurrentThread() {
        return this.lockSupport.isLockedByCurrentThread(this.getNodeEngine(), this.key);
    }

    @Override
    public int getLockCount() {
        return this.lockSupport.getLockCount(this.getNodeEngine(), this.key);
    }

    @Override
    public long getRemainingLeaseTime() {
        return this.lockSupport.getRemainingLeaseTime(this.getNodeEngine(), this.key);
    }

    @Override
    public void lock() {
        this.lockSupport.lock(this.getNodeEngine(), this.key);
    }

    @Override
    public void lock(long leaseTime, TimeUnit timeUnit) {
        Preconditions.checkPositive(leaseTime, "leaseTime should be positive");
        this.lockSupport.lock(this.getNodeEngine(), this.key, timeUnit.toMillis(leaseTime));
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        this.lockSupport.lockInterruptly(this.getNodeEngine(), this.key);
    }

    @Override
    public boolean tryLock() {
        return this.lockSupport.tryLock(this.getNodeEngine(), this.key);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        Preconditions.checkNotNull(unit, "unit can't be null");
        return this.lockSupport.tryLock(this.getNodeEngine(), this.key, time, unit);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit, long leaseTime, TimeUnit leaseUnit) throws InterruptedException {
        Preconditions.checkNotNull(unit, "unit can't be null");
        Preconditions.checkNotNull(leaseUnit, "lease unit can't be null");
        return this.lockSupport.tryLock(this.getNodeEngine(), this.key, time, unit, leaseTime, leaseUnit);
    }

    @Override
    public void unlock() {
        this.lockSupport.unlock(this.getNodeEngine(), this.key);
    }

    @Override
    public void forceUnlock() {
        this.lockSupport.forceUnlock(this.getNodeEngine(), this.key);
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("Use ILock.newCondition(String name) instead!");
    }

    @Override
    public ICondition newCondition(String name) {
        Preconditions.checkNotNull(name, "Condition name can't be null");
        return new ConditionImpl(this, name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    @Deprecated
    public Object getKey() {
        return this.getName();
    }

    public Data getKeyData() {
        return this.key;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    ObjectNamespace getNamespace() {
        return this.lockSupport.getNamespace();
    }

    @Override
    public String toString() {
        return "ILock{name='" + this.name + '\'' + '}';
    }
}

