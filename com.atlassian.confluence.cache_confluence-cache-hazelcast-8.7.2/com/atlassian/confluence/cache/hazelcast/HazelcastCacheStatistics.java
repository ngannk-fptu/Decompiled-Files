/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.confluence.cache.CacheStatistics
 *  com.atlassian.confluence.cache.CacheStatisticsHelper
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.google.common.base.Preconditions
 *  com.hazelcast.core.IMap
 *  com.hazelcast.monitor.LocalMapStats
 *  com.hazelcast.monitor.NearCacheStats
 */
package com.atlassian.confluence.cache.hazelcast;

import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.cache.CacheStatistics;
import com.atlassian.confluence.cache.CacheStatisticsHelper;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.google.common.base.Preconditions;
import com.hazelcast.core.IMap;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.monitor.NearCacheStats;

public class HazelcastCacheStatistics
implements CacheStatistics {
    private final String cacheName;
    private final String displayName;
    private final long hits;
    private final long ownedEntryCount;
    private final int maxSize;
    private final long heapCost;
    private final String formattedHeapCost;
    private final boolean nearCache;
    private final long misses;
    private final long expiredCount;

    public HazelcastCacheStatistics(ManagedCache cache, IMap<?, ?> iMap, FormatSettingsManager formatSettingsManager, I18NBean i18nBean) {
        this.cacheName = ((ManagedCache)Preconditions.checkNotNull((Object)cache)).getName();
        this.displayName = CacheStatisticsHelper.getDisplayableName((String)cache.getName(), (I18NBean)i18nBean);
        LocalMapStats localMapStats = iMap.getLocalMapStats();
        NearCacheStats nearCacheStats = localMapStats.getNearCacheStats();
        if (nearCacheStats != null) {
            this.hits = nearCacheStats.getHits();
            this.misses = nearCacheStats.getMisses();
            this.ownedEntryCount = nearCacheStats.getOwnedEntryCount();
            this.nearCache = true;
            double localEntryCount = localMapStats.getOwnedEntryCount();
            double nearCacheLocalEntryCount = nearCacheStats.getOwnedEntryCount();
            double ratio = localEntryCount != 0.0 ? nearCacheLocalEntryCount / localEntryCount : 0.0;
            this.heapCost = (long)((double)localMapStats.getHeapCost() * (1.0 + ratio));
        } else {
            this.hits = localMapStats.getHits();
            this.misses = -1L;
            this.ownedEntryCount = localMapStats.getOwnedEntryCount();
            this.nearCache = false;
            this.heapCost = localMapStats.getHeapCost();
        }
        Integer currentMaxEntries = cache.currentMaxEntries();
        this.maxSize = currentMaxEntries == null ? Integer.MAX_VALUE : currentMaxEntries;
        this.formattedHeapCost = CacheStatisticsHelper.formatSizeInMegabytes((FormatSettingsManager)formatSettingsManager, (long)this.heapCost);
        this.expiredCount = -1L;
    }

    public long getHitCount() {
        return this.hits;
    }

    public long getExpiredCount() {
        return this.expiredCount;
    }

    public long getAccessCount() {
        return this.getHitCount();
    }

    public long getMissCount() {
        return this.misses;
    }

    public int getHitPercent() {
        if (this.misses >= 0L && this.hits + this.misses >= 0L) {
            return CacheStatisticsHelper.asPercentage((long)this.hits, (long)(this.hits + this.misses));
        }
        return -1;
    }

    public long getSize() {
        return this.ownedEntryCount;
    }

    public long getMaxSize() {
        return this.maxSize;
    }

    public String getName() {
        return this.cacheName;
    }

    public int getUsagePercent() {
        return CacheStatisticsHelper.calculateCapacityPercentage((long)this.ownedEntryCount, (long)this.maxSize);
    }

    public String getNiceName() {
        return this.displayName;
    }

    public boolean hasContents() {
        return this.getSize() > 0L;
    }

    public String getFormattedSizeInMegabytes() {
        return this.formattedHeapCost;
    }

    public long getSizeInBytes() {
        return this.heapCost;
    }

    public boolean isNearCache() {
        return this.nearCache;
    }
}

