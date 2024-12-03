/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.ICondition;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Deprecated
public interface ILock
extends Lock,
DistributedObject {
    @Deprecated
    public Object getKey();

    @Override
    public void lock();

    @Override
    public boolean tryLock();

    @Override
    public boolean tryLock(long var1, TimeUnit var3) throws InterruptedException;

    public boolean tryLock(long var1, TimeUnit var3, long var4, TimeUnit var6) throws InterruptedException;

    @Override
    public void unlock();

    public void lock(long var1, TimeUnit var3);

    public void forceUnlock();

    @Override
    public Condition newCondition();

    public ICondition newCondition(String var1);

    public boolean isLocked();

    public boolean isLockedByCurrentThread();

    public int getLockCount();

    public long getRemainingLeaseTime();
}

