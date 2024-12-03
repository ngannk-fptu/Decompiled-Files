/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.util.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalCacheUtils {
    private static final Logger log = LoggerFactory.getLogger(LocalCacheUtils.class);

    private LocalCacheUtils() {
    }

    public static <K, V> ConcurrentMap<K, V> createExpiringAfterAccessMap(Duration ttl) {
        return LocalCacheUtils.createExpiringAfterAccessCache(ttl).asMap();
    }

    public static <K, V> ConcurrentMap<K, V> createExpiringAfterAccessMap(Duration ttl, ScheduledExecutorService cleanupPool) {
        Cache cache = LocalCacheUtils.createExpiringAfterAccessCache(ttl);
        cleanupPool.scheduleWithFixedDelay(() -> {
            try {
                cache.cleanUp();
            }
            catch (Exception e) {
                log.info("Error while cleaning up cache", (Throwable)e);
            }
        }, ttl.getSeconds(), ttl.getSeconds(), TimeUnit.SECONDS);
        return cache.asMap();
    }

    private static <K, V> Cache<K, V> createExpiringAfterAccessCache(Duration ttl) {
        return CacheBuilder.newBuilder().expireAfterAccess(ttl).build();
    }
}

