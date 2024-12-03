/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.ratelimiting.internal.concurrent;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LockGuard
implements AutoCloseable {
    private final Lock lock;

    protected LockGuard(@Nonnull Lock lock) {
        this.lock = Objects.requireNonNull(lock, "lock");
    }

    @Nonnull
    public static LockGuard lock(@Nonnull Lock lock) {
        Objects.requireNonNull(lock, "lock").lock();
        return new LockGuard(lock);
    }

    @Nonnull
    public static LockGuard lockInterruptibly(@Nonnull Lock lock) throws InterruptedException {
        Objects.requireNonNull(lock, "lock").lockInterruptibly();
        return new LockGuard(lock);
    }

    @Nullable
    public static LockGuard tryLock(@Nonnull Lock lock) {
        return Objects.requireNonNull(lock, "lock").tryLock() ? new LockGuard(lock) : null;
    }

    @Nullable
    public static LockGuard tryLock(@Nonnull Lock lock, long timeout, @Nonnull TimeUnit unit) throws InterruptedException {
        return Objects.requireNonNull(lock, "lock").tryLock(timeout, unit) ? new LockGuard(lock) : null;
    }

    @Override
    public void close() {
        this.lock.unlock();
    }
}

