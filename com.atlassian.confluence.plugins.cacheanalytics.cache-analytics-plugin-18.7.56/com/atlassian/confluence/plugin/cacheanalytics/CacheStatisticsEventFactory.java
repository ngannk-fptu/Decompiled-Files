/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.cacheanalytics;

import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.plugin.cacheanalytics.CacheNamePredicates;
import com.atlassian.confluence.plugin.cacheanalytics.events.CacheStatisticsEvent;
import com.atlassian.confluence.plugin.cacheanalytics.events.CacheType;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CacheStatisticsEventFactory {
    private final CacheManager cacheManager;
    private final ClusterManager clusterManager;
    private static final Set<CacheStatisticsKey> STATISTIC_KEYS = EnumSet.of(CacheStatisticsKey.SIZE, new CacheStatisticsKey[]{CacheStatisticsKey.HIT_COUNT, CacheStatisticsKey.MISS_COUNT, CacheStatisticsKey.LOAD_COUNT, CacheStatisticsKey.PUT_COUNT, CacheStatisticsKey.REMOVE_COUNT});

    @Autowired
    public CacheStatisticsEventFactory(@ComponentImport CacheManager cacheManager, @ComponentImport ClusterManager clusterManager) {
        this.cacheManager = cacheManager;
        this.clusterManager = clusterManager;
    }

    public Collection<CacheStatisticsEvent> createEvents() {
        return this.createEvents(this.clusterManager.getThisNodeInformation(), CacheStatisticsEventFactory.cacheNameFilter());
    }

    private Collection<CacheStatisticsEvent> createEvents(@Nullable ClusterNodeInformation nodeInfo, Predicate<String> cacheNameFilter) {
        return (Collection)this.cacheManager.getManagedCaches().stream().filter(cache -> cacheNameFilter.test(cache.getName())).map(cache -> this.createEvent((ManagedCache)cache, nodeInfo)).collect(ImmutableList.toImmutableList());
    }

    private static Predicate<String> cacheNameFilter() {
        return CacheNamePredicates.coreCacheNameFilter().orElse(name -> true);
    }

    private CacheStatisticsEvent createEvent(ManagedCache cache, @Nullable ClusterNodeInformation clusterNode) {
        return new CacheStatisticsEvent(cache.getName(), clusterNode == null ? null : clusterNode.getAnonymizedNodeIdentifier(), CacheType.forCache(cache), this.buildCacheStatistics(cache));
    }

    private Map<CacheStatisticsKey, Long> buildCacheStatistics(ManagedCache cache) {
        return CacheStatisticsEventFactory.reify(cache.getStatistics());
    }

    private static Map<CacheStatisticsKey, Long> reify(Map<CacheStatisticsKey, Supplier<Long>> statsMap) {
        return (Map)statsMap.entrySet().stream().filter(entry -> STATISTIC_KEYS.contains(entry.getKey())).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> (Long)((Supplier)entry.getValue()).get()));
    }
}

