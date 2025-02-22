/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.impl.querycache.QueryCacheConfigurator;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.QueryCacheEventService;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfoSupplier;
import com.hazelcast.map.impl.querycache.subscriber.InternalQueryCache;
import com.hazelcast.map.impl.querycache.subscriber.NullQueryCache;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheEndToEndConstructor;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheFactory;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheRequest;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContext;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberListener;
import com.hazelcast.query.Predicate;
import com.hazelcast.util.ExceptionUtil;

public abstract class AbstractQueryCacheEndToEndConstructor
implements QueryCacheEndToEndConstructor {
    protected static final int OPERATION_WAIT_TIMEOUT_MINUTES = 5;
    protected final String mapName;
    protected final QueryCacheRequest request;
    protected final QueryCacheContext context;
    protected final SubscriberContext subscriberContext;
    protected final ILogger logger = Logger.getLogger(this.getClass());
    protected InternalQueryCache queryCache;
    private Predicate predicate;
    private String publisherListenerId;

    public AbstractQueryCacheEndToEndConstructor(QueryCacheRequest request) {
        this.request = request;
        this.mapName = request.getMapName();
        this.context = request.getContext();
        this.subscriberContext = this.context.getSubscriberContext();
    }

    @Override
    public final void createSubscriberAccumulator(AccumulatorInfo info) {
        QueryCacheEventService eventService = this.context.getQueryCacheEventService();
        SubscriberListener listener = new SubscriberListener(this.context, info);
        this.publisherListenerId = eventService.addPublisherListener(info.getMapName(), info.getCacheId(), listener);
    }

    @Override
    public final InternalQueryCache createNew(String cacheId) {
        try {
            QueryCacheConfig queryCacheConfig = this.initQueryCacheConfig(this.request, cacheId);
            if (queryCacheConfig == null) {
                return NullQueryCache.NULL_QUERY_CACHE;
            }
            this.queryCache = this.createUnderlyingQueryCache(queryCacheConfig, this.request, cacheId);
            AccumulatorInfo info = AccumulatorInfo.toAccumulatorInfo(queryCacheConfig, this.mapName, cacheId, this.predicate);
            this.addInfoToSubscriberContext(info);
            info.setPublishable(true);
            String publisherListenerId = this.queryCache.getPublisherListenerId();
            if (publisherListenerId == null) {
                this.createSubscriberAccumulator(info);
            }
            this.createPublisherAccumulator(info);
            this.queryCache.setPublisherListenerId(this.publisherListenerId);
        }
        catch (Throwable throwable) {
            this.removeQueryCacheConfig(this.mapName, this.request.getCacheName());
            throw ExceptionUtil.rethrow(throwable);
        }
        return this.queryCache;
    }

    private InternalQueryCache createUnderlyingQueryCache(QueryCacheConfig queryCacheConfig, QueryCacheRequest request, String cacheId) {
        SubscriberContext subscriberContext = this.context.getSubscriberContext();
        QueryCacheFactory queryCacheFactory = subscriberContext.getQueryCacheFactory();
        request.withQueryCacheConfig(queryCacheConfig);
        return queryCacheFactory.create(request, cacheId);
    }

    private void addInfoToSubscriberContext(AccumulatorInfo info) {
        SubscriberContext subscriberContext = this.context.getSubscriberContext();
        AccumulatorInfoSupplier accumulatorInfoSupplier = subscriberContext.getAccumulatorInfoSupplier();
        accumulatorInfoSupplier.putIfAbsent(info.getMapName(), info.getCacheId(), info);
    }

    protected Object toObject(Object data) {
        return this.context.toObject(data);
    }

    private QueryCacheConfig initQueryCacheConfig(QueryCacheRequest request, String cacheId) {
        QueryCacheConfig queryCacheConfig;
        Predicate predicate = request.getPredicate();
        if (predicate == null) {
            queryCacheConfig = this.getOrNullQueryCacheConfig(this.mapName, request.getCacheName(), cacheId);
        } else {
            queryCacheConfig = this.getOrCreateQueryCacheConfig(this.mapName, request.getCacheName(), cacheId);
            queryCacheConfig.setIncludeValue(request.isIncludeValue());
            queryCacheConfig.getPredicateConfig().setImplementation(predicate);
        }
        if (queryCacheConfig == null) {
            return null;
        }
        this.predicate = queryCacheConfig.getPredicateConfig().getImplementation();
        return queryCacheConfig;
    }

    private QueryCacheConfig getOrCreateQueryCacheConfig(String mapName, String cacheName, String cacheId) {
        QueryCacheConfigurator queryCacheConfigurator = this.subscriberContext.geQueryCacheConfigurator();
        return queryCacheConfigurator.getOrCreateConfiguration(mapName, cacheName, cacheId);
    }

    private QueryCacheConfig getOrNullQueryCacheConfig(String mapName, String cacheName, String cacheId) {
        QueryCacheConfigurator queryCacheConfigurator = this.subscriberContext.geQueryCacheConfigurator();
        return queryCacheConfigurator.getOrNull(mapName, cacheName, cacheId);
    }

    private void removeQueryCacheConfig(String mapName, String cacheName) {
        QueryCacheConfigurator queryCacheConfigurator = this.subscriberContext.geQueryCacheConfigurator();
        queryCacheConfigurator.removeConfiguration(mapName, cacheName);
    }
}

