/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.failurecache;

import com.atlassian.failurecache.Cache;
import com.atlassian.failurecache.CacheFactory;
import com.atlassian.failurecache.CacheLoader;
import com.atlassian.failurecache.Cacheable;
import com.atlassian.failurecache.EagerCacheUpdatePolicy;
import com.atlassian.failurecache.ExpirationDateBasedCacheImpl;
import com.atlassian.failurecache.failures.ExponentialBackOffFailureCache;
import com.atlassian.failurecache.util.date.Clock;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

class CacheFactoryImpl
implements CacheFactory,
Cacheable {
    private final Clock clock;
    private final Set<Cacheable> failureCacheSet;
    private final Set<Cache> valueCacheSet;

    public CacheFactoryImpl(Clock clock) {
        this.clock = (Clock)Preconditions.checkNotNull((Object)clock);
        this.failureCacheSet = Collections.newSetFromMap(new WeakHashMap());
        this.valueCacheSet = Collections.newSetFromMap(new WeakHashMap());
    }

    @Override
    public <K, V> Cache<V> createExpirationDateBasedCache(CacheLoader<K, V> cacheLoader) {
        Preconditions.checkNotNull(cacheLoader, (Object)"cacheLoader");
        ExponentialBackOffFailureCache failureCache = new ExponentialBackOffFailureCache.Builder().clock(this.clock).build();
        return this.buildCache(cacheLoader, failureCache);
    }

    @Override
    public <K, V> Cache<V> createExpirationDateBasedCache(CacheLoader<K, V> cacheLoader, ExponentialBackOffFailureCache failureCache) {
        Preconditions.checkNotNull(cacheLoader, (Object)"cacheLoader");
        Preconditions.checkNotNull((Object)failureCache, (Object)"failureCache");
        return this.buildCache(cacheLoader, failureCache);
    }

    @Override
    public int getCachePriority() {
        if (this.failureCacheSet.isEmpty()) {
            return 200;
        }
        return this.failureCacheSet.iterator().next().getCachePriority();
    }

    @Override
    public void clearCache() {
        for (Cacheable failureCache : this.failureCacheSet) {
            failureCache.clearCache();
        }
        for (Cache cache : this.valueCacheSet) {
            cache.clear();
        }
    }

    private <K, V> ExpirationDateBasedCacheImpl<K, V> buildCache(CacheLoader<K, V> cacheLoader, ExponentialBackOffFailureCache<K> failureCache) {
        EagerCacheUpdatePolicy cacheUpdatePolicy = new EagerCacheUpdatePolicy(this.clock, failureCache);
        ExpirationDateBasedCacheImpl<K, V> cache = new ExpirationDateBasedCacheImpl<K, V>(cacheLoader, cacheUpdatePolicy);
        this.failureCacheSet.add(failureCache);
        this.valueCacheSet.add(cache);
        return cache;
    }
}

