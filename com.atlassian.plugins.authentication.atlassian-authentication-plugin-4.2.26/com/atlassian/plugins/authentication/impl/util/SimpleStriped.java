/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugins.authentication.impl.util;

import com.google.common.base.Preconditions;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class SimpleStriped {
    private final Lock[] sessionLocks;

    public SimpleStriped(int stripeCount) {
        Preconditions.checkArgument((stripeCount > 0 ? 1 : 0) != 0);
        this.sessionLocks = new Lock[stripeCount];
        for (int i = 0; i < this.sessionLocks.length; ++i) {
            this.sessionLocks[i] = new ReentrantLock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T withLock(Object lockKey, Supplier<T> effect) {
        int index = Math.abs(lockKey.hashCode() % this.sessionLocks.length);
        Lock lock = this.sessionLocks[index];
        lock.lock();
        try {
            T t = effect.get();
            return t;
        }
        finally {
            lock.unlock();
        }
    }
}

