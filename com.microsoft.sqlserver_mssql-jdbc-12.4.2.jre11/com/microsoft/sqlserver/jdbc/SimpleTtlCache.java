/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerSymmetricKey;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

final class SimpleTtlCache<K, V> {
    private static final Logger simpleCacheLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SimpleTtlCache");
    private static final long DEFAULT_TTL_IN_HOURS = 2L;
    private final ConcurrentHashMap<K, V> cache;
    private Duration cacheTtl = Duration.ofHours(2L);
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory(){

        @Override
        public Thread newThread(Runnable r) {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        }
    });

    int getCacheSize() {
        return this.cache.size();
    }

    void setCacheTtl(Duration duration) {
        this.cacheTtl = duration;
    }

    void setCacheTtl(long seconds) {
        this.cacheTtl = Duration.ofSeconds(seconds);
    }

    Duration getCacheTtl() {
        return this.cacheTtl;
    }

    SimpleTtlCache() {
        this.cache = new ConcurrentHashMap();
    }

    SimpleTtlCache(Duration duration) {
        this.cacheTtl = duration;
        this.cache = new ConcurrentHashMap();
    }

    boolean contains(Object key) {
        return this.cache.containsKey(key);
    }

    V get(Object key) {
        return this.cache.get(key);
    }

    V put(K key, V value) {
        V previousValue = null;
        long cacheTtlInSeconds = this.cacheTtl.getSeconds();
        if (0L < cacheTtlInSeconds) {
            previousValue = this.cache.put(key, value);
            if (simpleCacheLogger.isLoggable(Level.FINEST)) {
                simpleCacheLogger.fine("Adding encryption key to cache...");
            }
            scheduler.schedule(new CacheClear(key), cacheTtlInSeconds, TimeUnit.SECONDS);
        }
        return previousValue;
    }

    V put(K key, V value, Duration ttl) {
        V previousValue = null;
        long cacheTtlInSeconds = ttl.getSeconds();
        if (0L < cacheTtlInSeconds) {
            previousValue = this.cache.put(key, value);
            if (simpleCacheLogger.isLoggable(Level.FINEST)) {
                simpleCacheLogger.fine("Adding encryption key to cache...");
            }
            scheduler.schedule(new CacheClear(key), cacheTtlInSeconds, TimeUnit.SECONDS);
        }
        return previousValue;
    }

    class CacheClear
    implements Runnable {
        private K keylookupValue;
        private final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SimpleTtlCache.CacheClear");

        CacheClear(K keylookupValue) {
            this.keylookupValue = keylookupValue;
        }

        @Override
        public void run() {
            if (SimpleTtlCache.this.cache.containsKey(this.keylookupValue)) {
                Object value = SimpleTtlCache.this.cache.get(this.keylookupValue);
                if (value instanceof SQLServerSymmetricKey) {
                    ((SQLServerSymmetricKey)value).zeroOutKey();
                }
                SimpleTtlCache.this.cache.remove(this.keylookupValue);
                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine("Removed key from cache...");
                }
            }
        }
    }
}

