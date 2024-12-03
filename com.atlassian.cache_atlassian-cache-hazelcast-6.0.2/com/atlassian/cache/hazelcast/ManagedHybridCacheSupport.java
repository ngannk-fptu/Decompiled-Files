/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.ManagedCache
 *  javax.annotation.Nonnull
 */
package com.atlassian.cache.hazelcast;

import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.hazelcast.HazelcastCacheManager;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public abstract class ManagedHybridCacheSupport
implements ManagedCache {
    protected final HazelcastCacheManager cacheManager;
    protected final String name;

    public ManagedHybridCacheSupport(String name, HazelcastCacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.name = name;
    }

    public Long currentExpireAfterAccessMillis() {
        return this.getLocalCache().currentExpireAfterAccessMillis();
    }

    public Long currentExpireAfterWriteMillis() {
        return this.getLocalCache().currentExpireAfterWriteMillis();
    }

    public Integer currentMaxEntries() {
        return this.getLocalCache().currentMaxEntries();
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    public boolean isLocal() {
        return false;
    }

    public boolean isReplicateViaCopy() {
        return false;
    }

    public boolean updateExpireAfterAccess(long expireAfter, @Nonnull TimeUnit timeUnit) {
        return this.getLocalCache().updateExpireAfterAccess(expireAfter, timeUnit);
    }

    public boolean updateExpireAfterWrite(long expireAfter, @Nonnull TimeUnit timeUnit) {
        return this.getLocalCache().updateExpireAfterAccess(expireAfter, timeUnit);
    }

    public boolean updateMaxEntries(int newValue) {
        return this.getLocalCache().updateMaxEntries(newValue);
    }

    protected abstract ManagedCache getLocalCache();

    @Nonnull
    public SortedMap<CacheStatisticsKey, Supplier<Long>> getStatistics() {
        return this.getLocalCache().getStatistics();
    }

    public boolean isStatisticsEnabled() {
        return this.getLocalCache().isStatisticsEnabled();
    }

    public void setStatistics(boolean enabled) {
        this.getLocalCache().setStatistics(enabled);
    }
}

