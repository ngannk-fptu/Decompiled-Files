/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.ManagedCache
 *  com.google.common.collect.ImmutableSortedMap
 *  io.atlassian.fugue.Suppliers
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.hibernate.cache.spi.access.CachedDomainDataAccess
 *  org.hibernate.stat.CacheRegionStatistics
 */
package com.atlassian.confluence.impl.cache.hibernate;

import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.ManagedCache;
import com.google.common.collect.ImmutableSortedMap;
import io.atlassian.fugue.Suppliers;
import java.util.Comparator;
import java.util.Objects;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.hibernate.cache.spi.access.CachedDomainDataAccess;
import org.hibernate.stat.CacheRegionStatistics;

public final class ManagedHazelcastHibernateRegionCache
implements ManagedCache {
    private final String name;
    private final CacheRegionStatistics hibernateStats;
    private final CachedDomainDataAccess domainDataAccess;
    private final CacheSettings cacheSettings;

    ManagedHazelcastHibernateRegionCache(String name, CacheRegionStatistics hibernateStats, CachedDomainDataAccess domainDataAccess, CacheSettings cacheSettings) {
        this.name = Objects.requireNonNull(name);
        this.hibernateStats = Objects.requireNonNull(hibernateStats);
        this.domainDataAccess = Objects.requireNonNull(domainDataAccess);
        this.cacheSettings = Objects.requireNonNull(cacheSettings);
    }

    public void clear() {
        this.domainDataAccess.evictAll();
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public SortedMap<CacheStatisticsKey, Supplier<Long>> getStatistics() {
        return ImmutableSortedMap.orderedBy((Comparator)CacheStatisticsKey.SORT_BY_LABEL).put((Object)CacheStatisticsKey.HIT_COUNT, (Object)Suppliers.memoize(() -> ((CacheRegionStatistics)this.hibernateStats).getHitCount())).put((Object)CacheStatisticsKey.MISS_COUNT, (Object)Suppliers.memoize(() -> ((CacheRegionStatistics)this.hibernateStats).getMissCount())).put((Object)CacheStatisticsKey.PUT_COUNT, (Object)Suppliers.memoize(() -> ((CacheRegionStatistics)this.hibernateStats).getPutCount())).put((Object)CacheStatisticsKey.SIZE, (Object)Suppliers.memoize(() -> {
            long elementCountInMemory = this.hibernateStats.getElementCountInMemory();
            return elementCountInMemory == Long.MIN_VALUE ? 0L : elementCountInMemory;
        })).build();
    }

    public boolean isFlushable() {
        return this.cacheSettings.getFlushable(true);
    }

    @Nullable
    public Integer currentMaxEntries() {
        return this.cacheSettings.getMaxEntries();
    }

    public boolean updateMaxEntries(int newValue) {
        return false;
    }

    public Long currentExpireAfterAccessMillis() {
        return this.cacheSettings.getExpireAfterAccess();
    }

    public boolean updateExpireAfterAccess(long expireAfter, TimeUnit timeUnit) {
        return false;
    }

    @Nullable
    public Long currentExpireAfterWriteMillis() {
        return this.cacheSettings.getExpireAfterWrite();
    }

    public boolean updateExpireAfterWrite(long expireAfter, TimeUnit timeUnit) {
        return false;
    }

    public boolean isLocal() {
        return this.cacheSettings.getLocal(false);
    }

    public boolean isReplicateAsynchronously() {
        return this.cacheSettings.getReplicateAsynchronously(false);
    }

    public boolean isReplicateViaCopy() {
        return this.cacheSettings.getReplicateViaCopy(false);
    }

    public boolean isStatisticsEnabled() {
        return true;
    }

    public void setStatistics(boolean enabled) {
    }
}

