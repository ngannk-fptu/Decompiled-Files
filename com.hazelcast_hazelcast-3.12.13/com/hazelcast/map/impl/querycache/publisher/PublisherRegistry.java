/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.publisher;

import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.Registry;
import com.hazelcast.map.impl.querycache.accumulator.Accumulator;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorFactory;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfoSupplier;
import com.hazelcast.map.impl.querycache.publisher.PartitionAccumulatorRegistry;
import com.hazelcast.map.impl.querycache.publisher.PublisherAccumulatorFactory;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.Preconditions;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PublisherRegistry
implements Registry<String, PartitionAccumulatorRegistry> {
    private final ConstructorFunction<String, PartitionAccumulatorRegistry> partitionAccumulatorRegistryConstructor = new ConstructorFunction<String, PartitionAccumulatorRegistry>(){

        @Override
        public PartitionAccumulatorRegistry createNew(String cacheId) {
            AccumulatorInfo info = PublisherRegistry.this.getAccumulatorInfo(cacheId);
            Preconditions.checkNotNull(info, "info cannot be null");
            PublisherAccumulatorFactory accumulatorFactory = PublisherRegistry.this.createPublisherAccumulatorFactory();
            PublisherAccumulatorConstructor constructor = new PublisherAccumulatorConstructor(info, accumulatorFactory);
            return new PartitionAccumulatorRegistry(info, constructor);
        }
    };
    private final String mapName;
    private final QueryCacheContext context;
    private final ConcurrentMap<String, PartitionAccumulatorRegistry> partitionAccumulators;

    public PublisherRegistry(QueryCacheContext context, String mapName) {
        this.context = context;
        this.mapName = mapName;
        this.partitionAccumulators = new ConcurrentHashMap<String, PartitionAccumulatorRegistry>();
    }

    @Override
    public PartitionAccumulatorRegistry getOrCreate(String cacheId) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.partitionAccumulators, cacheId, this.partitionAccumulatorRegistryConstructor);
    }

    @Override
    public PartitionAccumulatorRegistry getOrNull(String cacheId) {
        return (PartitionAccumulatorRegistry)this.partitionAccumulators.get(cacheId);
    }

    @Override
    public Map<String, PartitionAccumulatorRegistry> getAll() {
        return Collections.unmodifiableMap(this.partitionAccumulators);
    }

    @Override
    public PartitionAccumulatorRegistry remove(String cacheId) {
        return (PartitionAccumulatorRegistry)this.partitionAccumulators.remove(cacheId);
    }

    private AccumulatorInfo getAccumulatorInfo(String cacheId) {
        PublisherContext publisherContext = this.context.getPublisherContext();
        AccumulatorInfoSupplier infoSupplier = publisherContext.getAccumulatorInfoSupplier();
        return infoSupplier.getAccumulatorInfoOrNull(this.mapName, cacheId);
    }

    private PublisherAccumulatorFactory createPublisherAccumulatorFactory() {
        return new PublisherAccumulatorFactory(this.context);
    }

    private static class PublisherAccumulatorConstructor
    implements ConstructorFunction<Integer, Accumulator> {
        private final AccumulatorInfo info;
        private final AccumulatorFactory accumulatorFactory;

        PublisherAccumulatorConstructor(AccumulatorInfo info, AccumulatorFactory accumulatorFactory) {
            this.info = info;
            this.accumulatorFactory = accumulatorFactory;
        }

        @Override
        public Accumulator createNew(Integer partitionId) {
            return this.accumulatorFactory.createAccumulator(this.info);
        }
    }
}

