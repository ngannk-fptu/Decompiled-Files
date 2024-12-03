/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache;

import com.hazelcast.core.IFunction;
import com.hazelcast.core.IMapEvent;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.Member;
import com.hazelcast.instance.LifecycleServiceImpl;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.querycache.InvokerWrapper;
import com.hazelcast.map.impl.querycache.NodeInvokerWrapper;
import com.hazelcast.map.impl.querycache.QueryCacheConfigurator;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.QueryCacheEventService;
import com.hazelcast.map.impl.querycache.QueryCacheScheduler;
import com.hazelcast.map.impl.querycache.publisher.DefaultPublisherContext;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.map.impl.querycache.subscriber.NodeQueryCacheConfigurator;
import com.hazelcast.map.impl.querycache.subscriber.NodeQueryCacheEventService;
import com.hazelcast.map.impl.querycache.subscriber.NodeQueryCacheScheduler;
import com.hazelcast.map.impl.querycache.subscriber.NodeSubscriberContext;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContext;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.util.ContextMutexFactory;
import java.util.ArrayList;
import java.util.Collection;

public class NodeQueryCacheContext
implements QueryCacheContext {
    private final NodeEngine nodeEngine;
    private final InvokerWrapper invokerWrapper;
    private final MapServiceContext mapServiceContext;
    private final QueryCacheScheduler queryCacheScheduler;
    private final QueryCacheEventService queryCacheEventService;
    private final QueryCacheConfigurator queryCacheConfigurator;
    private final ContextMutexFactory lifecycleMutexFactory = new ContextMutexFactory();
    private PublisherContext publisherContext;
    private SubscriberContext subscriberContext;

    public NodeQueryCacheContext(MapServiceContext mapServiceContext) {
        this.nodeEngine = mapServiceContext.getNodeEngine();
        this.mapServiceContext = mapServiceContext;
        this.queryCacheScheduler = new NodeQueryCacheScheduler(mapServiceContext);
        this.queryCacheEventService = new NodeQueryCacheEventService(mapServiceContext, this.lifecycleMutexFactory);
        this.queryCacheConfigurator = new NodeQueryCacheConfigurator(this.nodeEngine.getConfig(), this.nodeEngine.getConfigClassLoader(), this.queryCacheEventService);
        this.invokerWrapper = new NodeInvokerWrapper(this.nodeEngine.getOperationService());
        this.subscriberContext = new NodeSubscriberContext(this);
        this.publisherContext = new DefaultPublisherContext(this, this.nodeEngine, new RegisterMapListenerFunction());
        this.flushPublishersOnNodeShutdown();
    }

    private void flushPublishersOnNodeShutdown() {
        Node node = ((NodeEngineImpl)this.nodeEngine).getNode();
        LifecycleServiceImpl lifecycleService = node.hazelcastInstance.getLifecycleService();
        lifecycleService.addLifecycleListener(new LifecycleListener(){

            @Override
            public void stateChanged(LifecycleEvent event) {
                if (LifecycleEvent.LifecycleState.SHUTTING_DOWN == event.getState()) {
                    NodeQueryCacheContext.this.publisherContext.flush();
                }
            }
        });
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PublisherContext getPublisherContext() {
        return this.publisherContext;
    }

    @Override
    public SubscriberContext getSubscriberContext() {
        return this.subscriberContext;
    }

    @Override
    public void setSubscriberContext(SubscriberContext subscriberContext) {
        this.subscriberContext = subscriberContext;
    }

    @Override
    public QueryCacheEventService getQueryCacheEventService() {
        return this.queryCacheEventService;
    }

    @Override
    public QueryCacheConfigurator getQueryCacheConfigurator() {
        return this.queryCacheConfigurator;
    }

    @Override
    public QueryCacheScheduler getQueryCacheScheduler() {
        return this.queryCacheScheduler;
    }

    @Override
    public InternalSerializationService getSerializationService() {
        return (InternalSerializationService)this.nodeEngine.getSerializationService();
    }

    @Override
    public Address getThisNodesAddress() {
        return this.nodeEngine.getThisAddress();
    }

    @Override
    public Collection<Member> getMemberList() {
        Collection<MemberImpl> memberList = this.nodeEngine.getClusterService().getMemberImpls();
        ArrayList<Member> members = new ArrayList<Member>(memberList.size());
        members.addAll(memberList);
        return members;
    }

    @Override
    public int getPartitionId(Object object) {
        assert (object != null);
        if (object instanceof Data) {
            this.nodeEngine.getPartitionService().getPartitionId((Data)object);
        }
        return this.nodeEngine.getPartitionService().getPartitionId(object);
    }

    @Override
    public int getPartitionCount() {
        return this.nodeEngine.getPartitionService().getPartitionCount();
    }

    @Override
    public InvokerWrapper getInvokerWrapper() {
        return this.invokerWrapper;
    }

    @Override
    public Object toObject(Object obj) {
        return this.mapServiceContext.toObject(obj);
    }

    @Override
    public ContextMutexFactory getLifecycleMutexFactory() {
        return this.lifecycleMutexFactory;
    }

    private String registerLocalIMapListener(final String name) {
        return this.mapServiceContext.addLocalListenerAdapter(new ListenerAdapter<IMapEvent>(){

            @Override
            public void onEvent(IMapEvent event) {
            }

            public String toString() {
                return "Local IMap listener for the map '" + name + "'";
            }
        }, name);
    }

    @SerializableByConvention
    private class RegisterMapListenerFunction
    implements IFunction<String, String> {
        private RegisterMapListenerFunction() {
        }

        @Override
        public String apply(String name) {
            return NodeQueryCacheContext.this.registerLocalIMapListener(name);
        }
    }
}

