/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.cluster.hazelcast.DualLock;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Internal
class JvmDualLock
implements DualLock {
    private volatile Serializable value;
    private final ReentrantLock lock = new ReentrantLock();

    JvmDualLock() {
    }

    public void lock() {
        this.lock.lock();
    }

    public boolean tryLock() {
        return this.lock.tryLock();
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return this.lock.tryLock(time, unit);
    }

    public void lockInterruptibly() throws InterruptedException {
        this.lock.lockInterruptibly();
    }

    public boolean isHeldByCurrentThread() {
        return this.lock.isHeldByCurrentThread();
    }

    public void unlock() {
        this.lock.unlock();
    }

    public Serializable getValue() {
        return this.value;
    }

    public void setValue(Serializable value) {
        this.value = value;
    }

    public Condition newCondition() {
        throw new UnsupportedOperationException("newCondition() not supported");
    }
}

