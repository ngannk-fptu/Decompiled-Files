/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.core.IMap;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.util.Preconditions;

public class QueryCacheRequest {
    private IMap map;
    private String mapName;
    private String cacheName;
    private Predicate predicate;
    private MapListener listener;
    private Boolean includeValue;
    private QueryCacheContext context;
    private QueryCacheConfig queryCacheConfig;

    QueryCacheRequest() {
    }

    public static QueryCacheRequest newQueryCacheRequest() {
        return new QueryCacheRequest();
    }

    public QueryCacheRequest forMap(IMap map) {
        this.map = Preconditions.checkNotNull(map, "map cannot be null");
        this.mapName = map.getName();
        return this;
    }

    public QueryCacheRequest withCacheName(String cacheName) {
        this.cacheName = Preconditions.checkHasText(cacheName, "cacheName");
        return this;
    }

    public QueryCacheRequest withPredicate(Predicate predicate) {
        Preconditions.checkNotInstanceOf(PagingPredicate.class, predicate, "predicate");
        this.predicate = predicate;
        return this;
    }

    public QueryCacheRequest withListener(MapListener listener) {
        this.listener = listener;
        return this;
    }

    public QueryCacheRequest withIncludeValue(Boolean includeValue) {
        this.includeValue = includeValue;
        return this;
    }

    public QueryCacheRequest withContext(QueryCacheContext context) {
        this.context = Preconditions.checkNotNull(context, "context can not be null");
        return this;
    }

    public QueryCacheRequest withQueryCacheConfig(QueryCacheConfig queryCacheConfig) {
        this.queryCacheConfig = Preconditions.checkNotNull(queryCacheConfig, "queryCacheConfig can not be null");
        return this;
    }

    public IMap getMap() {
        return this.map;
    }

    public String getMapName() {
        return this.mapName;
    }

    public String getCacheName() {
        return this.cacheName;
    }

    public Predicate getPredicate() {
        return this.predicate;
    }

    public MapListener getListener() {
        return this.listener;
    }

    public Boolean isIncludeValue() {
        return this.includeValue;
    }

    public QueryCacheContext getContext() {
        return this.context;
    }

    public QueryCacheConfig getQueryCacheConfig() {
        return this.queryCacheConfig;
    }
}

