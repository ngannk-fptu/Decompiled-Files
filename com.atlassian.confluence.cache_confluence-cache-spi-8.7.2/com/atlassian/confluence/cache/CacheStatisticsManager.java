/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheStatisticsKey
 */
package com.atlassian.confluence.cache;

import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.confluence.cache.CacheStatistics;
import com.atlassian.confluence.cache.CacheStatisticsCapability;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public interface CacheStatisticsManager {
    public static final String CACHE_NAME_PREFIX = "cache.name.";

    @Deprecated
    public List<CacheStatistics> getLocalCacheStatistics();

    @Deprecated
    public CacheStatistics getLocalCacheStatistics(String var1);

    public Set<CacheStatisticsCapability> getCapabilities();

    default public Predicate<CacheStatisticsKey> getCacheStatisticFilter(String cacheName) {
        return key -> true;
    }
}

