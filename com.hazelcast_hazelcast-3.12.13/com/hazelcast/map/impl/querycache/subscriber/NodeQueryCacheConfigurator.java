/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.internal.config.ConfigUtils;
import com.hazelcast.map.impl.querycache.QueryCacheEventService;
import com.hazelcast.map.impl.querycache.subscriber.AbstractQueryCacheConfigurator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class NodeQueryCacheConfigurator
extends AbstractQueryCacheConfigurator {
    private final Config config;

    public NodeQueryCacheConfigurator(Config config, ClassLoader configClassLoader, QueryCacheEventService eventService) {
        super(configClassLoader, eventService);
        this.config = config;
    }

    @Override
    public QueryCacheConfig getOrCreateConfiguration(String mapName, String cacheName, String cacheId) {
        MapConfig mapConfig = this.config.getMapConfig(mapName);
        QueryCacheConfig queryCacheConfig = this.findQueryCacheConfigFromMapConfig(mapConfig, cacheName);
        if (queryCacheConfig != null) {
            this.setPredicateImpl(queryCacheConfig);
            this.setEntryListener(mapName, cacheId, queryCacheConfig);
            return queryCacheConfig;
        }
        QueryCacheConfig newConfig = new QueryCacheConfig(cacheName);
        mapConfig.getQueryCacheConfigs().add(newConfig);
        return newConfig;
    }

    @Override
    public QueryCacheConfig getOrNull(String mapName, String cacheName, String cacheId) {
        MapConfig mapConfig = this.config.getMapConfigOrNull(mapName);
        if (mapConfig == null) {
            return null;
        }
        QueryCacheConfig queryCacheConfig = this.findQueryCacheConfigFromMapConfig(mapConfig, cacheName);
        if (queryCacheConfig != null) {
            this.setPredicateImpl(queryCacheConfig);
            this.setEntryListener(mapName, cacheId, queryCacheConfig);
            return queryCacheConfig;
        }
        return queryCacheConfig;
    }

    private QueryCacheConfig findQueryCacheConfigFromMapConfig(MapConfig mapConfig, String cacheName) {
        List<QueryCacheConfig> queryCacheConfigs = mapConfig.getQueryCacheConfigs();
        HashMap<String, QueryCacheConfig> allQueryCacheConfigs = new HashMap<String, QueryCacheConfig>(queryCacheConfigs.size());
        for (QueryCacheConfig queryCacheConfig : queryCacheConfigs) {
            allQueryCacheConfigs.put(queryCacheConfig.getName(), queryCacheConfig);
        }
        return (QueryCacheConfig)ConfigUtils.lookupByPattern(this.config.getConfigPatternMatcher(), allQueryCacheConfigs, cacheName);
    }

    @Override
    public void removeConfiguration(String mapName, String cacheName) {
        MapConfig mapConfig = this.config.getMapConfig(mapName);
        List<QueryCacheConfig> queryCacheConfigs = mapConfig.getQueryCacheConfigs();
        if (queryCacheConfigs == null || queryCacheConfigs.isEmpty()) {
            return;
        }
        Iterator<QueryCacheConfig> iterator = queryCacheConfigs.iterator();
        while (iterator.hasNext()) {
            QueryCacheConfig config = iterator.next();
            if (!config.getName().equals(cacheName)) continue;
            iterator.remove();
        }
    }
}

