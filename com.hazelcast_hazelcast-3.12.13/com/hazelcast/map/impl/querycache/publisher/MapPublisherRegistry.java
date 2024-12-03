/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.publisher;

import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.Registry;
import com.hazelcast.map.impl.querycache.publisher.PublisherRegistry;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapPublisherRegistry
implements Registry<String, PublisherRegistry> {
    private final ConstructorFunction<String, PublisherRegistry> registryConstructorFunction = new ConstructorFunction<String, PublisherRegistry>(){

        @Override
        public PublisherRegistry createNew(String mapName) {
            return MapPublisherRegistry.this.createPublisherRegistry(mapName);
        }
    };
    private final QueryCacheContext context;
    private final ConcurrentMap<String, PublisherRegistry> cachePublishersPerIMap;

    public MapPublisherRegistry(QueryCacheContext context) {
        this.context = context;
        this.cachePublishersPerIMap = new ConcurrentHashMap<String, PublisherRegistry>();
    }

    @Override
    public PublisherRegistry getOrCreate(String mapName) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.cachePublishersPerIMap, mapName, this.registryConstructorFunction);
    }

    @Override
    public PublisherRegistry getOrNull(String mapName) {
        return (PublisherRegistry)this.cachePublishersPerIMap.get(mapName);
    }

    @Override
    public Map<String, PublisherRegistry> getAll() {
        return Collections.unmodifiableMap(this.cachePublishersPerIMap);
    }

    @Override
    public PublisherRegistry remove(String id) {
        return (PublisherRegistry)this.cachePublishersPerIMap.remove(id);
    }

    private PublisherRegistry createPublisherRegistry(String mapName) {
        return new PublisherRegistry(this.context, mapName);
    }
}

