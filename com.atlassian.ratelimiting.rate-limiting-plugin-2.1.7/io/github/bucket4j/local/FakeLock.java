/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.local;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class FakeLock
implements Lock {
    public static final Lock INSTANCE = new FakeLock();

    private FakeLock() {
    }

    @Override
    public void lock() {
    }

    @Override
    public void unlock() {
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}

