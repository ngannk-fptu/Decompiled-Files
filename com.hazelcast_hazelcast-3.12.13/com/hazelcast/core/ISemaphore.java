/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;
import java.util.concurrent.TimeUnit;

public interface ISemaphore
extends DistributedObject {
    @Override
    public String getName();

    public boolean init(int var1);

    public void acquire() throws InterruptedException;

    public void acquire(int var1) throws InterruptedException;

    public int availablePermits();

    public int drainPermits();

    public void reducePermits(int var1);

    public void increasePermits(int var1);

    public void release();

    public void release(int var1);

    public boolean tryAcquire();

    public boolean tryAcquire(int var1);

    public boolean tryAcquire(long var1, TimeUnit var3) throws InterruptedException;

    public boolean tryAcquire(int var1, long var2, TimeUnit var4) throws InterruptedException;
}

