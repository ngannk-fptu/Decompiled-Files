/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.spi.ClientAwareService;

class MapClientAwareService
implements ClientAwareService {
    private final MapServiceContext mapServiceContext;

    public MapClientAwareService(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
    }

    @Override
    public void clientDisconnected(String clientUuid) {
        QueryCacheContext queryCacheContext = this.mapServiceContext.getQueryCacheContext();
        PublisherContext publisherContext = queryCacheContext.getPublisherContext();
        publisherContext.handleDisconnectedSubscriber(clientUuid);
    }
}

