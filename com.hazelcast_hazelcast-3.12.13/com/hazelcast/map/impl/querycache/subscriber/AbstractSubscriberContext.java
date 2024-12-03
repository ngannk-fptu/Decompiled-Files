/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.map.impl.querycache.QueryCacheConfigurator;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.QueryCacheEventService;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfoSupplier;
import com.hazelcast.map.impl.querycache.accumulator.DefaultAccumulatorInfoSupplier;
import com.hazelcast.map.impl.querycache.subscriber.MapSubscriberRegistry;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheEndToEndProvider;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheFactory;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContext;

public abstract class AbstractSubscriberContext
implements SubscriberContext {
    private final QueryCacheEventService eventService;
    private final QueryCacheEndToEndProvider queryCacheEndToEndProvider;
    private final MapSubscriberRegistry mapSubscriberRegistry;
    private final QueryCacheConfigurator queryCacheConfigurator;
    private final QueryCacheFactory queryCacheFactory;
    private final DefaultAccumulatorInfoSupplier accumulatorInfoSupplier;

    public AbstractSubscriberContext(QueryCacheContext context) {
        this.queryCacheConfigurator = context.getQueryCacheConfigurator();
        this.eventService = context.getQueryCacheEventService();
        this.queryCacheEndToEndProvider = new QueryCacheEndToEndProvider(context.getLifecycleMutexFactory());
        this.mapSubscriberRegistry = new MapSubscriberRegistry(context);
        this.queryCacheFactory = new QueryCacheFactory();
        this.accumulatorInfoSupplier = new DefaultAccumulatorInfoSupplier();
    }

    @Override
    public QueryCacheEndToEndProvider getEndToEndQueryCacheProvider() {
        return this.queryCacheEndToEndProvider;
    }

    @Override
    public MapSubscriberRegistry getMapSubscriberRegistry() {
        return this.mapSubscriberRegistry;
    }

    @Override
    public QueryCacheFactory getQueryCacheFactory() {
        return this.queryCacheFactory;
    }

    @Override
    public AccumulatorInfoSupplier getAccumulatorInfoSupplier() {
        return this.accumulatorInfoSupplier;
    }

    @Override
    public QueryCacheEventService getEventService() {
        return this.eventService;
    }

    @Override
    public QueryCacheConfigurator geQueryCacheConfigurator() {
        return this.queryCacheConfigurator;
    }
}

