/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.hazelcast.core.HazelcastInstance
 */
package com.atlassian.confluence.cache.hazelcast;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.confluence.cache.hazelcast.AsyncInvalidationCacheFactory;
import com.atlassian.confluence.impl.cache.hazelcast.ConfluenceHazelcastCacheManager;
import com.hazelcast.core.HazelcastInstance;

@Deprecated(forRemoval=true)
@Internal
final class HazelcastCacheManagerFactory {
    private final CacheFactory localCacheFactory;
    private final CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider;
    private final AsyncInvalidationCacheFactory asyncInvalidationCacheFactory;
    private final HazelcastInstance hazelcastInstance;

    public HazelcastCacheManagerFactory(HazelcastInstance hazelcastInstance, CacheFactory localCacheFactory, CacheSettingsDefaultsProvider cacheSettingsDefaultsProvider, AsyncInvalidationCacheFactory asyncInvalidationCacheFactory) {
        this.asyncInvalidationCacheFactory = asyncInvalidationCacheFactory;
        this.hazelcastInstance = hazelcastInstance;
        this.localCacheFactory = localCacheFactory;
        this.cacheSettingsDefaultsProvider = cacheSettingsDefaultsProvider;
    }

    CacheManager create() {
        ConfluenceHazelcastCacheManager cacheManager = new ConfluenceHazelcastCacheManager(this.hazelcastInstance, this.localCacheFactory, this.cacheSettingsDefaultsProvider, this.asyncInvalidationCacheFactory);
        cacheManager.init();
        return cacheManager;
    }
}

