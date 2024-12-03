/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.google.common.collect.ImmutableSortedMap
 *  io.atlassian.util.concurrent.Suppliers
 *  net.sf.ehcache.statistics.StatisticsGateway
 */
package com.atlassian.cache.ehcache;

import com.atlassian.cache.CacheStatisticsKey;
import com.google.common.collect.ImmutableSortedMap;
import io.atlassian.util.concurrent.Suppliers;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.function.Supplier;
import net.sf.ehcache.statistics.StatisticsGateway;

public class DelegatingCacheStatistics {
    public static SortedMap<CacheStatisticsKey, Supplier<Long>> toStatistics(final StatisticsGateway stats) {
        Supplier<Long> heapSize = new Supplier<Long>(){

            @Override
            public Long get() {
                return stats.getLocalHeapSizeInBytes();
            }
        };
        return ImmutableSortedMap.orderedBy((Comparator)CacheStatisticsKey.SORT_BY_LABEL).put((Object)CacheStatisticsKey.SIZE, (Object)Suppliers.memoize((Object)stats.getSize())).put((Object)CacheStatisticsKey.HEAP_SIZE, (Object)heapSize).put((Object)CacheStatisticsKey.HIT_COUNT, (Object)Suppliers.memoize((Object)stats.cacheHitCount())).put((Object)CacheStatisticsKey.PUT_COUNT, (Object)Suppliers.memoize((Object)stats.cachePutCount())).put((Object)CacheStatisticsKey.REMOVE_COUNT, (Object)Suppliers.memoize((Object)stats.cacheRemoveCount())).put((Object)CacheStatisticsKey.MISS_COUNT, (Object)Suppliers.memoize((Object)stats.cacheMissCount())).put((Object)CacheStatisticsKey.EVICTION_COUNT, (Object)Suppliers.memoize((Object)stats.cacheEvictedCount())).build();
    }
}

