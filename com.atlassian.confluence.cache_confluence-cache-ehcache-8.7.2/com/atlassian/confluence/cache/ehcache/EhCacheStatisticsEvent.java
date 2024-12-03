/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 *  net.sf.ehcache.statistics.FlatStatistics
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.cache.ehcache;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;
import java.io.Serializable;
import java.util.Objects;
import net.sf.ehcache.statistics.FlatStatistics;
import org.checkerframework.checker.nullness.qual.NonNull;

@AsynchronousPreferred
@EventName(value="confluence.ehcache.statistics")
public class EhCacheStatisticsEvent
implements Serializable {
    private final String cacheName;
    private final long numberOfElementsOnTheHeap;
    private final long hitCount;
    private final long missExpiredCount;
    private final long missNotFoundCount;
    private final long evictedCount;
    private final long expiredCount;

    static EhCacheStatisticsEvent create(String cacheName, FlatStatistics stats) {
        return new EhCacheStatisticsEvent(cacheName, stats.getLocalHeapSize(), stats.cacheHitCount(), stats.cacheMissExpiredCount(), stats.cacheMissNotFoundCount(), stats.cacheEvictedCount(), stats.cacheExpiredCount());
    }

    private EhCacheStatisticsEvent(String cacheName, long numberOfElementsOnTheHeap, long hitCount, long missExpiredCount, long missNotFoundCount, long evictedCount, long expiredCount) {
        this.cacheName = Objects.requireNonNull(cacheName);
        this.numberOfElementsOnTheHeap = numberOfElementsOnTheHeap;
        this.hitCount = hitCount;
        this.missExpiredCount = missExpiredCount;
        this.missNotFoundCount = missNotFoundCount;
        this.evictedCount = evictedCount;
        this.expiredCount = expiredCount;
    }

    public @NonNull String getCacheName() {
        return this.cacheName;
    }

    public long getNumberOfElementsOnTheHeap() {
        return this.numberOfElementsOnTheHeap;
    }

    public long getHitCount() {
        return this.hitCount;
    }

    public long getMissExpiredCount() {
        return this.missExpiredCount;
    }

    public long getMissNotFoundCount() {
        return this.missNotFoundCount;
    }

    public long getEvictedCount() {
        return this.evictedCount;
    }

    public long getExpiredCount() {
        return this.expiredCount;
    }
}

