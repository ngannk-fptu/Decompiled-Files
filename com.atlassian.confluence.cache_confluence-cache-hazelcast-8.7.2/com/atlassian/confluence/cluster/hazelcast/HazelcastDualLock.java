/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.base.Preconditions
 *  com.hazelcast.core.IMap
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.cluster.hazelcast.DualLock;
import com.google.common.base.Preconditions;
import com.hazelcast.core.IMap;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

@Internal
class HazelcastDualLock
implements DualLock {
    private final IMap<String, Serializable> lockMap;
    private final String key;

    HazelcastDualLock(IMap<String, Serializable> lockMap, String key) {
        this.lockMap = (IMap)Preconditions.checkNotNull(lockMap);
        this.key = (String)Preconditions.checkNotNull((Object)key);
    }

    public void lock() {
        this.lockMap.lock((Object)this.key);
    }

    public boolean tryLock() {
        return this.lockMap.tryLock((Object)this.key);
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return this.lockMap.tryLock((Object)this.key, time, unit);
    }

    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException("lockInterruptibly() not supported");
    }

    public boolean isHeldByCurrentThread() {
        throw new UnsupportedOperationException("isHeldByCurrentThread() not supported");
    }

    public void unlock() {
        this.lockMap.unlock((Object)this.key);
    }

    public Serializable getValue() {
        return (Serializable)this.lockMap.get((Object)this.key);
    }

    public void setValue(Serializable value) {
        this.lockMap.set((Object)this.key, (Object)((Serializable)Preconditions.checkNotNull((Object)value)));
    }

    public Condition newCondition() {
        throw new UnsupportedOperationException("newCondition() not supported");
    }
}

