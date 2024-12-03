/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.event.api.AsynchronousPreferred
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugin.cacheanalytics.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.confluence.plugin.cacheanalytics.EventUtil;
import com.atlassian.confluence.plugin.cacheanalytics.events.CacheType;
import com.atlassian.event.api.AsynchronousPreferred;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

@EventName(value="confluence.cache.statistics")
@AsynchronousPreferred
public class CacheStatisticsEvent {
    private final String cacheName;
    private final String clusterNodeId;
    private final CacheType cacheType;
    private final Map<CacheStatisticsKey, Long> statistics;

    public CacheStatisticsEvent(String cacheName, @Nullable String clusterNodeId, CacheType cacheType, Map<CacheStatisticsKey, Long> statistics) {
        this.cacheName = cacheName;
        this.clusterNodeId = clusterNodeId;
        this.cacheType = cacheType;
        this.statistics = statistics;
    }

    public String getCacheName() {
        return this.cacheName;
    }

    public int getCacheNameHash() {
        return EventUtil.simpleHash(this.cacheName);
    }

    public CacheType getCacheType() {
        return this.cacheType;
    }

    @Nullable
    public String getClusterNodeId() {
        return this.clusterNodeId;
    }

    @Nullable
    public Long getHitCount() {
        return this.statistics.get(CacheStatisticsKey.HIT_COUNT);
    }

    @Nullable
    public Long getMissCount() {
        return this.statistics.get(CacheStatisticsKey.MISS_COUNT);
    }

    @Nullable
    public Long getLoadCount() {
        return this.statistics.get(CacheStatisticsKey.LOAD_COUNT);
    }

    @Nullable
    public Long getPutCount() {
        return this.statistics.get(CacheStatisticsKey.PUT_COUNT);
    }

    @Nullable
    public Long getRemoveCount() {
        return this.statistics.get(CacheStatisticsKey.REMOVE_COUNT);
    }

    @Nullable
    public Long getSize() {
        return this.statistics.get(CacheStatisticsKey.SIZE);
    }

    public String toString() {
        return "ManagedCacheStatisticsEvent{cacheName='" + this.cacheName + "', clusterNodeId='" + this.clusterNodeId + "', cacheType=" + this.cacheType + ", statistics=" + this.statistics + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CacheStatisticsEvent that = (CacheStatisticsEvent)o;
        return this.cacheName.equals(that.cacheName) && Objects.equals(this.clusterNodeId, that.clusterNodeId) && this.cacheType == that.cacheType && this.statistics.equals(that.statistics);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.cacheName, this.clusterNodeId, this.cacheType, this.statistics});
    }
}

