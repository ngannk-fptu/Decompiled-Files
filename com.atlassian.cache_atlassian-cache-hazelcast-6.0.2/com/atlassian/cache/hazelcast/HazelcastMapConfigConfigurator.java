/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheSettings
 *  com.hazelcast.config.EvictionConfig
 *  com.hazelcast.config.EvictionPolicy
 *  com.hazelcast.config.MapConfig
 *  com.hazelcast.config.MaxSizeConfig
 *  com.hazelcast.config.MaxSizeConfig$MaxSizePolicy
 *  com.hazelcast.config.NearCacheConfig
 *  com.hazelcast.config.NearCachePreloaderConfig
 *  javax.annotation.Nonnull
 */
package com.atlassian.cache.hazelcast;

import com.atlassian.cache.CacheSettings;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.NearCachePreloaderConfig;
import java.util.Random;
import javax.annotation.Nonnull;

class HazelcastMapConfigConfigurator {
    static final int HYBRID_MULTIPLIER = 2;
    static final int SMALL_CACHES_CAPACITY_MULTIPLIER = 2;
    static final int NEAR_CACHE_EXPIRY_RATIO = Integer.getInteger("atlassian.cache.nearCacheExpiryRatio", 75);

    HazelcastMapConfigConfigurator() {
    }

    static MapConfig configureMapConfig(CacheSettings settings, MapConfig mapConfig, int partitionsCount) {
        boolean nearCache;
        Long expireAfterWrite;
        Long expireAfterAccess;
        NearCacheConfig nearCacheConfig;
        boolean hybrid = !settings.getReplicateViaCopy(true);
        Integer multiplier = hybrid ? 2 : 1;
        Integer maxEntries = settings.getMaxEntries();
        NearCacheConfig nearCacheConfig2 = nearCacheConfig = mapConfig.getNearCacheConfig() == null ? new NearCacheConfig() : HazelcastMapConfigConfigurator.copyNearCacheConfig(mapConfig.getNearCacheConfig());
        if (maxEntries != null) {
            int maxSize = HazelcastMapConfigConfigurator.adjustPerNodeCapacity(mapConfig, multiplier * maxEntries, partitionsCount);
            nearCacheConfig.setMaxSize(maxSize);
            nearCacheConfig.setEvictionPolicy(EvictionPolicy.LFU.name());
        }
        if ((expireAfterAccess = settings.getExpireAfterAccess()) != null) {
            int maxIdleSeconds = multiplier * HazelcastMapConfigConfigurator.roundUpToWholeSeconds(expireAfterAccess);
            mapConfig.setMaxIdleSeconds(maxIdleSeconds);
            int jitter = new Random().nextInt(30) - 15;
            int nearCacheTtl = (int)Math.floor((double)((NEAR_CACHE_EXPIRY_RATIO + jitter) * maxIdleSeconds) / 100.0);
            nearCacheConfig.setTimeToLiveSeconds(Math.max(1, nearCacheTtl));
        }
        if ((expireAfterWrite = settings.getExpireAfterWrite()) != null) {
            int timeToLiveSeconds = multiplier * HazelcastMapConfigConfigurator.roundUpToWholeSeconds(expireAfterWrite);
            mapConfig.setTimeToLiveSeconds(timeToLiveSeconds);
            nearCacheConfig.setTimeToLiveSeconds(timeToLiveSeconds);
        }
        if (nearCache = settings.getReplicateAsynchronously(true)) {
            mapConfig.setNearCacheConfig(nearCacheConfig);
        } else {
            mapConfig.setNearCacheConfig(null);
        }
        return mapConfig;
    }

    private static NearCacheConfig copyNearCacheConfig(@Nonnull NearCacheConfig nearCacheConfig) {
        NearCacheConfig nearCacheConfigCopy = new NearCacheConfig(nearCacheConfig);
        EvictionConfig evictionConfigCopy = new EvictionConfig(nearCacheConfig.getEvictionConfig());
        nearCacheConfigCopy.setEvictionConfig(evictionConfigCopy);
        NearCachePreloaderConfig preloaderConfigCopy = new NearCachePreloaderConfig(nearCacheConfigCopy.getPreloaderConfig());
        nearCacheConfigCopy.setPreloaderConfig(preloaderConfigCopy);
        return nearCacheConfigCopy;
    }

    private static int adjustPerNodeCapacity(MapConfig mapConfig, int desiredPerNodeSize, int partitionsCount) {
        int adjustedCacheSize = Math.max(2 * partitionsCount, desiredPerNodeSize);
        mapConfig.setMaxSizeConfig(new MaxSizeConfig().setMaxSizePolicy(MaxSizeConfig.MaxSizePolicy.PER_NODE).setSize(adjustedCacheSize));
        mapConfig.setEvictionPolicy(EvictionPolicy.LFU);
        return adjustedCacheSize;
    }

    private static int roundUpToWholeSeconds(Long expireAfterAccess) {
        return (int)Math.ceil((double)expireAfterAccess.longValue() / 1000.0);
    }
}

