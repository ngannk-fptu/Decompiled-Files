/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache;

import com.hazelcast.core.Member;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.querycache.InvokerWrapper;
import com.hazelcast.map.impl.querycache.QueryCacheConfigurator;
import com.hazelcast.map.impl.querycache.QueryCacheEventService;
import com.hazelcast.map.impl.querycache.QueryCacheScheduler;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContext;
import com.hazelcast.nio.Address;
import com.hazelcast.util.ContextMutexFactory;
import java.util.Collection;

public interface QueryCacheContext {
    public PublisherContext getPublisherContext();

    public SubscriberContext getSubscriberContext();

    public InternalSerializationService getSerializationService();

    public QueryCacheEventService getQueryCacheEventService();

    public QueryCacheConfigurator getQueryCacheConfigurator();

    public QueryCacheScheduler getQueryCacheScheduler();

    public Collection<Member> getMemberList();

    public InvokerWrapper getInvokerWrapper();

    public Object toObject(Object var1);

    public Address getThisNodesAddress();

    public int getPartitionId(Object var1);

    public int getPartitionCount();

    public ContextMutexFactory getLifecycleMutexFactory();

    public void destroy();

    public void setSubscriberContext(SubscriberContext var1);
}

