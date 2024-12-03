/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.atlassian.cache.hazelcast.HazelcastCacheManager
 *  com.hazelcast.core.HazelcastInstance
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cache.hazelcast;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.cache.hazelcast.HazelcastCacheManager;
import com.atlassian.confluence.cache.hazelcast.AsyncInvalidationCacheFactory;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfluenceHazelcastCacheManager
extends HazelcastCacheManager {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceHazelcastCacheManager.class);
    private final AsyncInvalidationCacheFactory asyncInvalidationCacheFactory;

    public ConfluenceHazelcastCacheManager(HazelcastInstance hazelcast, CacheFactory localCacheFactory, CacheSettingsDefaultsProvider settingsDefaultsProvider, AsyncInvalidationCacheFactory asyncInvalidationCacheFactory) {
        super(hazelcast, localCacheFactory, settingsDefaultsProvider);
        this.asyncInvalidationCacheFactory = asyncInvalidationCacheFactory;
    }

    protected <K, V> Cache<K, V> createAsyncHybridCache(String cacheName, CacheLoader<K, V> loader, CacheSettings settings) {
        return this.asyncInvalidationCacheFactory.createInvalidationCache(cacheName, loader, settings);
    }

    protected <K, V> Cache<K, V> createDistributedCache(String name, CacheLoader<K, V> loader, CacheSettings settings) {
        if (settings.getReplicateAsynchronously(false) && this.asyncInvalidationCacheFactory.isReplicatedCacheSupported(name)) {
            log.warn("Creating experimental asynchronous replicate-via-copy cache [{}]", (Object)name);
            return this.asyncInvalidationCacheFactory.createReplicatedCache(name, loader, settings);
        }
        log.warn("Creating replicate-via-copy cache [{}]. These cache are non-performant. Prefer replicate-via-invalidation instead.", (Object)name);
        return super.createDistributedCache(name, loader, settings);
    }
}

