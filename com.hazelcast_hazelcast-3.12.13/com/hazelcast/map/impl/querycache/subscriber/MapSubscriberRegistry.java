/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.Registry;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberRegistry;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapSubscriberRegistry
implements Registry<String, SubscriberRegistry> {
    private final ConstructorFunction<String, SubscriberRegistry> registryConstructorFunction = new ConstructorFunction<String, SubscriberRegistry>(){

        @Override
        public SubscriberRegistry createNew(String mapName) {
            return MapSubscriberRegistry.this.createSubscriberRegistry(mapName);
        }
    };
    private final QueryCacheContext context;
    private final ConcurrentMap<String, SubscriberRegistry> cachePublishersPerIMap;

    public MapSubscriberRegistry(QueryCacheContext context) {
        this.context = context;
        this.cachePublishersPerIMap = new ConcurrentHashMap<String, SubscriberRegistry>();
    }

    @Override
    public SubscriberRegistry getOrCreate(String mapName) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.cachePublishersPerIMap, mapName, this.registryConstructorFunction);
    }

    @Override
    public SubscriberRegistry getOrNull(String mapName) {
        return (SubscriberRegistry)this.cachePublishersPerIMap.get(mapName);
    }

    @Override
    public Map<String, SubscriberRegistry> getAll() {
        return Collections.unmodifiableMap(this.cachePublishersPerIMap);
    }

    @Override
    public SubscriberRegistry remove(String mapName) {
        return (SubscriberRegistry)this.cachePublishersPerIMap.remove(mapName);
    }

    protected SubscriberRegistry createSubscriberRegistry(String mapName) {
        return new SubscriberRegistry(this.context, mapName);
    }

    protected QueryCacheContext getContext() {
        return this.context;
    }
}

