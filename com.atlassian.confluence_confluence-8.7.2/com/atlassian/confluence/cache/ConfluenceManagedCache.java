/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.instrumentation.caches.CacheCollector
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.cache;

import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.ManagedCache;
import com.atlassian.instrumentation.caches.CacheCollector;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ConfluenceManagedCache
implements ManagedCache {
    private final ManagedCache delegate;
    private final boolean flushable;

    public ConfluenceManagedCache(ManagedCache delegate, boolean flushable) {
        this.delegate = delegate;
        this.flushable = flushable;
    }

    public void clear() {
        this.delegate.clear();
    }

    public String getName() {
        return this.delegate.getName();
    }

    public boolean isFlushable() {
        return this.delegate.isFlushable() && this.flushable;
    }

    public Integer currentMaxEntries() {
        return this.delegate.currentMaxEntries();
    }

    public boolean updateMaxEntries(int newValue) {
        return this.delegate.updateMaxEntries(newValue);
    }

    public Long currentExpireAfterAccessMillis() {
        return this.delegate.currentExpireAfterAccessMillis();
    }

    public boolean updateExpireAfterAccess(long expireAfter, TimeUnit timeUnit) {
        return this.delegate.updateExpireAfterAccess(expireAfter, timeUnit);
    }

    public Long currentExpireAfterWriteMillis() {
        return this.delegate.currentExpireAfterWriteMillis();
    }

    public boolean updateExpireAfterWrite(long expireAfter, TimeUnit timeUnit) {
        return this.delegate.updateExpireAfterWrite(expireAfter, timeUnit);
    }

    public boolean isLocal() {
        return this.delegate.isLocal();
    }

    public boolean isReplicateAsynchronously() {
        return this.delegate.isReplicateAsynchronously();
    }

    public boolean isReplicateViaCopy() {
        return this.delegate.isReplicateViaCopy();
    }

    public boolean isStatisticsEnabled() {
        return this.delegate.isStatisticsEnabled();
    }

    public void setStatistics(boolean b) {
        this.delegate.setStatistics(b);
    }

    public @NonNull SortedMap<CacheStatisticsKey, Supplier<Long>> getStatistics() {
        return this.delegate.getStatistics();
    }

    public @Nullable CacheCollector getCacheCollector() {
        return this.delegate.getCacheCollector();
    }
}

