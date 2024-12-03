/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.subscriber.AbstractSubscriberContext;
import com.hazelcast.map.impl.querycache.subscriber.NodeQueryCacheEndToEndConstructor;
import com.hazelcast.map.impl.querycache.subscriber.NodeSubscriberContextSupport;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheEndToEndConstructor;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheRequest;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContextSupport;

public class NodeSubscriberContext
extends AbstractSubscriberContext {
    private final SubscriberContextSupport subscriberContextSupport;

    public NodeSubscriberContext(QueryCacheContext context) {
        super(context);
        this.subscriberContextSupport = new NodeSubscriberContextSupport(context.getSerializationService());
    }

    @Override
    public SubscriberContextSupport getSubscriberContextSupport() {
        return this.subscriberContextSupport;
    }

    @Override
    public QueryCacheEndToEndConstructor newEndToEndConstructor(QueryCacheRequest request) {
        return new NodeQueryCacheEndToEndConstructor(request);
    }
}

