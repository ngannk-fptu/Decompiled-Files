/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.confluence.cache.CacheStatistics
 *  com.atlassian.confluence.cache.CacheStatisticsCapability
 *  com.atlassian.confluence.cache.CacheStatisticsManager
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList$Builder
 *  com.hazelcast.core.IMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache.hazelcast;

import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.cache.CacheStatistics;
import com.atlassian.confluence.cache.CacheStatisticsCapability;
import com.atlassian.confluence.cache.CacheStatisticsManager;
import com.atlassian.confluence.cache.hazelcast.HazelcastCacheStatistics;
import com.atlassian.confluence.cache.hazelcast.HazelcastHelper;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.hazelcast.core.IMap;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HazelcastStatisticsManager
implements CacheStatisticsManager {
    private static final Logger log = LoggerFactory.getLogger(HazelcastStatisticsManager.class);
    private final CacheManager cacheManager;
    private final FormatSettingsManager formatSettingsManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final CacheStatisticsManager localCacheStatisticsManager;
    private final HazelcastHelper hazelcastHelper;

    public HazelcastStatisticsManager(CacheManager cacheManager, FormatSettingsManager formatSettingsManager, I18NBeanFactory i18NBeanFactory, CacheStatisticsManager localCacheStatisticsManager, HazelcastHelper hazelcastHelper) {
        this.cacheManager = cacheManager;
        this.formatSettingsManager = formatSettingsManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localCacheStatisticsManager = localCacheStatisticsManager;
        this.hazelcastHelper = hazelcastHelper;
    }

    public Predicate<CacheStatisticsKey> getCacheStatisticFilter(String cacheName) {
        return this.localCacheStatisticsManager.getCacheStatisticFilter(cacheName);
    }

    public List<CacheStatistics> getLocalCacheStatistics() {
        ImmutableList.Builder listBuilder = new ImmutableList.Builder();
        for (ManagedCache managedCache : this.cacheManager.getManagedCaches()) {
            listBuilder.add((Object)this.getLocalCacheStatisticsInternal(managedCache));
        }
        return listBuilder.build();
    }

    public CacheStatistics getLocalCacheStatistics(String cacheName) {
        ManagedCache managedCache = (ManagedCache)Preconditions.checkNotNull((Object)this.cacheManager.getManagedCache((String)Preconditions.checkNotNull((Object)cacheName)), (Object)("Cannot find a cache named " + cacheName));
        return this.getLocalCacheStatisticsInternal(managedCache);
    }

    private CacheStatistics getLocalCacheStatisticsInternal(ManagedCache managedCache) {
        if (managedCache.isLocal()) {
            log.trace("Delegating cache stats to local cache manager");
            return this.localCacheStatisticsManager.getLocalCacheStatistics(managedCache.getName());
        }
        return this.getStatsForCache(managedCache);
    }

    public Set<CacheStatisticsCapability> getCapabilities() {
        return Collections.emptySet();
    }

    private HazelcastCacheStatistics getStatsForCache(ManagedCache managedCache) {
        IMap hazelcastMap = this.hazelcastHelper.getHazelcastMapForCache(managedCache.getName());
        return new HazelcastCacheStatistics(managedCache, hazelcastMap, this.formatSettingsManager, this.i18NBeanFactory.getI18NBean());
    }
}

