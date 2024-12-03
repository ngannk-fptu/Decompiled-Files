/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableMap
 *  org.joda.time.Duration
 */
package com.atlassian.confluence.plugins.emailgateway.blacklist;

import com.atlassian.confluence.plugins.emailgateway.blacklist.Blacklist;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.joda.time.Duration;

public class RealTimeBlacklist<T>
implements Blacklist<T> {
    private static final int DEFAULT_MAX_CACHE_SIZE = 1000;
    private static final int DEFAULT_BLACKLIST_THRESHOLD = 100;
    public static final Duration DEFAULT_EXPIRY_DURATION = Duration.standardHours((long)1L);
    private final LoadingCache<T, AtomicInteger> counts;
    private final int maxCacheSize;
    private final int blacklistThreshold;
    private final Duration expiryDuration;

    public RealTimeBlacklist() {
        this(1000, 100, DEFAULT_EXPIRY_DURATION);
    }

    public RealTimeBlacklist(int maxCacheSize, int blacklistThreshold, Duration expiryDuration) {
        this.maxCacheSize = maxCacheSize;
        this.blacklistThreshold = blacklistThreshold;
        this.expiryDuration = expiryDuration;
        this.counts = CacheBuilder.newBuilder().maximumSize((long)maxCacheSize).expireAfterWrite(expiryDuration.getMillis(), TimeUnit.MILLISECONDS).build(new CacheLoader<T, AtomicInteger>(){

            public AtomicInteger load(T internetAddress) {
                return new AtomicInteger();
            }
        });
    }

    public Map<T, ? extends Number> getCurrentCounts() {
        return ImmutableMap.copyOf((Map)this.counts.asMap());
    }

    void cleanup() {
        this.counts.cleanUp();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean incrementAndCheckBlacklist(T key) {
        AtomicInteger count = (AtomicInteger)this.counts.getUnchecked(key);
        try {
            boolean bl = this.exceedsBlacklistThreshold(count.incrementAndGet());
            return bl;
        }
        finally {
            this.counts.asMap().put(key, count);
        }
    }

    @Override
    public boolean isBlackListed(T key) {
        int count = ((AtomicInteger)this.counts.getUnchecked(key)).get();
        return this.exceedsBlacklistThreshold(count);
    }

    private boolean exceedsBlacklistThreshold(int count) {
        return count > this.blacklistThreshold;
    }

    public int getMaxCacheSize() {
        return this.maxCacheSize;
    }

    public int getBlacklistThreshold() {
        return this.blacklistThreshold;
    }

    public Duration getExpiryDuration() {
        return this.expiryDuration;
    }
}

