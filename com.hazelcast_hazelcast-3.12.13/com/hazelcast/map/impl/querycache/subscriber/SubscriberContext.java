/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.map.impl.querycache.QueryCacheConfigurator;
import com.hazelcast.map.impl.querycache.QueryCacheEventService;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfoSupplier;
import com.hazelcast.map.impl.querycache.subscriber.MapSubscriberRegistry;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheEndToEndConstructor;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheEndToEndProvider;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheFactory;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheRequest;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContextSupport;

public interface SubscriberContext {
    public QueryCacheEventService getEventService();

    public QueryCacheEndToEndProvider getEndToEndQueryCacheProvider();

    public QueryCacheConfigurator geQueryCacheConfigurator();

    public QueryCacheFactory getQueryCacheFactory();

    public AccumulatorInfoSupplier getAccumulatorInfoSupplier();

    public MapSubscriberRegistry getMapSubscriberRegistry();

    public SubscriberContextSupport getSubscriberContextSupport();

    public QueryCacheEndToEndConstructor newEndToEndConstructor(QueryCacheRequest var1);
}

