/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.lock;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.cp.CPGroupId;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public interface FencedLock
extends Lock,
DistributedObject {
    public static final long INVALID_FENCE = 0L;

    @Override
    public void lock();

    @Override
    public void lockInterruptibly() throws InterruptedException;

    public long lockAndGetFence();

    @Override
    public boolean tryLock();

    public long tryLockAndGetFence();

    @Override
    public boolean tryLock(long var1, TimeUnit var3);

    public long tryLockAndGetFence(long var1, TimeUnit var3);

    @Override
    public void unlock();

    public long getFence();

    public boolean isLocked();

    public boolean isLockedByCurrentThread();

    public int getLockCount();

    public CPGroupId getGroupId();

    @Override
    public Condition newCondition();
}

