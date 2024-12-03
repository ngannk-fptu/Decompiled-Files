/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.VCacheException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.vcache.internal.core.service;

import com.atlassian.vcache.VCacheException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VCacheLock {
    private static final Logger log = LoggerFactory.getLogger(VCacheLock.class);
    private final ReentrantLock lock = new ReentrantLock();
    private final String cacheName;
    private final long lockTimeoutMillis;

    public VCacheLock(String cacheName, Duration lockTimeout) {
        this.cacheName = Objects.requireNonNull(cacheName);
        this.lockTimeoutMillis = lockTimeout.toMillis();
    }

    public <R> R withLock(Supplier<R> supplier) {
        this.lockWithTimeout();
        try {
            R r = supplier.get();
            return r;
        }
        finally {
            this.lock.unlock();
        }
    }

    public void withLock(Runnable runner) {
        this.lockWithTimeout();
        try {
            runner.run();
        }
        finally {
            this.lock.unlock();
        }
    }

    private void lockWithTimeout() {
        try {
            if (!this.lock.tryLock(this.lockTimeoutMillis, TimeUnit.MILLISECONDS)) {
                log.warn("Timed out waiting for lock on cache: {}", (Object)this.cacheName);
                throw new VCacheException("Timed out waiting for lock on cache: " + this.cacheName);
            }
        }
        catch (InterruptedException e) {
            Thread.interrupted();
            log.warn("Interrupted whilst waiting for a lock on cache: ", (Object)this.cacheName, (Object)e);
            throw new VCacheException("Interrupted waiting for lock on cache: " + this.cacheName, (Throwable)e);
        }
    }
}

