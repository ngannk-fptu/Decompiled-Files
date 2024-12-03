/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.Registry;
import com.hazelcast.map.impl.querycache.accumulator.Accumulator;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfoSupplier;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberAccumulatorFactory;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContext;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.Preconditions;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SubscriberRegistry
implements Registry<String, Accumulator> {
    private final ConstructorFunction<String, Accumulator> accumulatorConstructor = new ConstructorFunction<String, Accumulator>(){

        @Override
        public Accumulator createNew(String cacheId) {
            AccumulatorInfo info = SubscriberRegistry.this.getAccumulatorInfo(cacheId);
            Preconditions.checkNotNull(info, "info cannot be null");
            SubscriberAccumulatorFactory accumulatorFactory = SubscriberRegistry.this.createSubscriberAccumulatorFactory();
            return accumulatorFactory.createAccumulator(info);
        }
    };
    private final String mapName;
    private final QueryCacheContext context;
    private final ConcurrentMap<String, Accumulator> accumulators;

    public SubscriberRegistry(QueryCacheContext context, String mapName) {
        this.context = context;
        this.mapName = mapName;
        this.accumulators = new ConcurrentHashMap<String, Accumulator>();
    }

    @Override
    public Accumulator getOrCreate(String cacheId) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.accumulators, cacheId, this.accumulatorConstructor);
    }

    @Override
    public Accumulator getOrNull(String cacheId) {
        return (Accumulator)this.accumulators.get(cacheId);
    }

    @Override
    public Map<String, Accumulator> getAll() {
        return Collections.unmodifiableMap(this.accumulators);
    }

    @Override
    public Accumulator remove(String cacheId) {
        return (Accumulator)this.accumulators.remove(cacheId);
    }

    private AccumulatorInfo getAccumulatorInfo(String cacheId) {
        SubscriberContext subscriberContext = this.context.getSubscriberContext();
        AccumulatorInfoSupplier infoSupplier = subscriberContext.getAccumulatorInfoSupplier();
        return infoSupplier.getAccumulatorInfoOrNull(this.mapName, cacheId);
    }

    protected SubscriberAccumulatorFactory createSubscriberAccumulatorFactory() {
        return new SubscriberAccumulatorFactory(this.context);
    }

    protected QueryCacheContext getContext() {
        return this.context;
    }
}

