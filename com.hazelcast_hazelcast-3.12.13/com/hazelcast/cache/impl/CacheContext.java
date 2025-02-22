/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CacheContext {
    private final AtomicLong entryCount = new AtomicLong(0L);
    private final AtomicInteger cacheEntryListenerCount = new AtomicInteger(0);
    private final AtomicInteger invalidationListenerCount = new AtomicInteger(0);

    public long getEntryCount() {
        return this.entryCount.get();
    }

    public long increaseEntryCount() {
        return this.entryCount.incrementAndGet();
    }

    public long increaseEntryCount(long count) {
        return this.entryCount.addAndGet(count);
    }

    public long decreaseEntryCount() {
        return this.entryCount.decrementAndGet();
    }

    public long decreaseEntryCount(long count) {
        return this.entryCount.addAndGet(-count);
    }

    public void resetEntryCount() {
        this.entryCount.set(0L);
    }

    public int getCacheEntryListenerCount() {
        return this.cacheEntryListenerCount.get();
    }

    public void increaseCacheEntryListenerCount() {
        this.cacheEntryListenerCount.incrementAndGet();
    }

    public void decreaseCacheEntryListenerCount() {
        this.cacheEntryListenerCount.decrementAndGet();
    }

    public void resetCacheEntryListenerCount() {
        this.cacheEntryListenerCount.set(0);
    }

    public int getInvalidationListenerCount() {
        return this.invalidationListenerCount.get();
    }

    public void increaseInvalidationListenerCount() {
        this.invalidationListenerCount.incrementAndGet();
    }

    public void decreaseInvalidationListenerCount() {
        this.invalidationListenerCount.decrementAndGet();
    }

    public void resetInvalidationListenerCount() {
        this.invalidationListenerCount.set(0);
    }

    public String toString() {
        return "CacheContext{entryCount=" + this.entryCount.get() + ", cacheEntryListenerCount=" + this.cacheEntryListenerCount.get() + ", invalidationListenerCount=" + this.invalidationListenerCount.get() + '}';
    }
}

