/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cache.CacheStatistics
 *  com.atlassian.confluence.cache.CacheStatisticsManager
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.jmx;

import com.atlassian.confluence.cache.CacheStatistics;
import com.atlassian.confluence.cache.CacheStatisticsManager;
import com.atlassian.confluence.jmx.CacheStatisticsMXBean;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

public class JxmCacheStatisticsWrapper
implements CacheStatisticsMXBean {
    private final CacheStatisticsManager cacheStatisticsManager;

    public JxmCacheStatisticsWrapper(CacheStatisticsManager cacheStatisticsManager) {
        this.cacheStatisticsManager = cacheStatisticsManager;
    }

    @Override
    public CacheStatistics[] getCacheStatisticsAsArray() {
        List stats = this.cacheStatisticsManager.getLocalCacheStatistics();
        return stats.toArray(new CacheStatistics[stats.size()]);
    }

    @Override
    public Map<String, CacheStatistics> getCacheStatisticsAsMap() {
        List stats = this.cacheStatisticsManager.getLocalCacheStatistics();
        return Maps.uniqueIndex((Iterable)stats, CacheStatistics::getName);
    }
}

