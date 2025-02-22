/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.utils;

import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.Accumulator;
import com.hazelcast.map.impl.querycache.publisher.MapPublisherRegistry;
import com.hazelcast.map.impl.querycache.publisher.PartitionAccumulatorRegistry;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.map.impl.querycache.publisher.PublisherRegistry;
import java.util.Collections;
import java.util.Map;

public final class QueryCacheUtil {
    private QueryCacheUtil() {
    }

    public static Map<Integer, Accumulator> getAccumulators(QueryCacheContext context, String mapName, String cacheId) {
        PartitionAccumulatorRegistry partitionAccumulatorRegistry = QueryCacheUtil.getAccumulatorRegistryOrNull(context, mapName, cacheId);
        if (partitionAccumulatorRegistry == null) {
            return Collections.emptyMap();
        }
        return partitionAccumulatorRegistry.getAll();
    }

    public static PartitionAccumulatorRegistry getAccumulatorRegistryOrNull(QueryCacheContext context, String mapName, String cacheId) {
        PublisherContext publisherContext = context.getPublisherContext();
        MapPublisherRegistry mapPublisherRegistry = publisherContext.getMapPublisherRegistry();
        PublisherRegistry publisherRegistry = mapPublisherRegistry.getOrNull(mapName);
        if (publisherRegistry == null) {
            return null;
        }
        return publisherRegistry.getOrNull(cacheId);
    }

    public static Accumulator getAccumulatorOrNull(QueryCacheContext context, String mapName, String cacheId, int partitionId) {
        PartitionAccumulatorRegistry accumulatorRegistry = QueryCacheUtil.getAccumulatorRegistryOrNull(context, mapName, cacheId);
        if (accumulatorRegistry == null) {
            return null;
        }
        return accumulatorRegistry.getOrNull(partitionId);
    }
}

