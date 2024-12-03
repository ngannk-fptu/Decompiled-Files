/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheContext;
import com.hazelcast.cache.impl.CacheEventData;
import com.hazelcast.cache.impl.CacheEventListener;
import com.hazelcast.cache.impl.CacheEventSet;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheAddEntryListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.ListenerWrapperEventFilter;
import com.hazelcast.spi.NotifiableEventListener;
import java.security.Permission;
import java.util.Set;
import java.util.concurrent.Callable;

public class CacheAddEntryListenerMessageTask
extends AbstractCallableMessageTask<CacheAddEntryListenerCodec.RequestParameters>
implements ListenerMessageTask {
    public CacheAddEntryListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() {
        final CacheService service = (CacheService)this.getService("hz:impl:cacheService");
        CacheEntryListener cacheEntryListener = new CacheEntryListener(this.endpoint, this);
        final String registrationId = service.registerListener(((CacheAddEntryListenerCodec.RequestParameters)this.parameters).name, cacheEntryListener, cacheEntryListener, ((CacheAddEntryListenerCodec.RequestParameters)this.parameters).localOnly);
        this.endpoint.addDestroyAction(registrationId, new Callable<Boolean>(){

            @Override
            public Boolean call() throws Exception {
                return service.deregisterListener(((CacheAddEntryListenerCodec.RequestParameters)((CacheAddEntryListenerMessageTask)CacheAddEntryListenerMessageTask.this).parameters).name, registrationId);
            }
        });
        return registrationId;
    }

    @Override
    protected CacheAddEntryListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheAddEntryListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheAddEntryListenerCodec.encodeResponse((String)response);
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheAddEntryListenerCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new CachePermission(((CacheAddEntryListenerCodec.RequestParameters)this.parameters).name, "listen");
    }

    @Override
    public String getMethodName() {
        return "registerCacheEntryListener";
    }

    private static final class CacheEntryListener
    implements CacheEventListener,
    NotifiableEventListener<CacheService>,
    ListenerWrapperEventFilter {
        private final ClientEndpoint endpoint;
        private final CacheAddEntryListenerMessageTask cacheAddEntryListenerMessageTask;

        private CacheEntryListener(ClientEndpoint endpoint, CacheAddEntryListenerMessageTask cacheAddEntryListenerMessageTask) {
            this.endpoint = endpoint;
            this.cacheAddEntryListenerMessageTask = cacheAddEntryListenerMessageTask;
        }

        private Data getPartitionKey(Object eventObject) {
            Data partitionKey = null;
            if (eventObject instanceof CacheEventSet) {
                Set<CacheEventData> events = ((CacheEventSet)eventObject).getEvents();
                if (events.size() > 1) {
                    partitionKey = new HeapData();
                } else if (events.size() == 1) {
                    partitionKey = events.iterator().next().getDataKey();
                }
            } else if (eventObject instanceof CacheEventData) {
                partitionKey = ((CacheEventData)eventObject).getDataKey();
            }
            return partitionKey;
        }

        @Override
        public void handleEvent(Object eventObject) {
            if (!this.endpoint.isAlive()) {
                return;
            }
            if (eventObject instanceof CacheEventSet) {
                CacheEventSet ces = (CacheEventSet)eventObject;
                Data partitionKey = this.getPartitionKey(eventObject);
                ClientMessage clientMessage = CacheAddEntryListenerCodec.encodeCacheEvent(ces.getEventType().getType(), ces.getEvents(), ces.getCompletionId());
                this.cacheAddEntryListenerMessageTask.sendClientMessage(partitionKey, clientMessage);
            }
        }

        @Override
        public void onRegister(CacheService service, String serviceName, String topic, EventRegistration registration) {
            CacheContext cacheContext = service.getOrCreateCacheContext(topic);
            cacheContext.increaseCacheEntryListenerCount();
        }

        @Override
        public void onDeregister(CacheService service, String serviceName, String topic, EventRegistration registration) {
            CacheContext cacheContext = service.getOrCreateCacheContext(topic);
            cacheContext.decreaseCacheEntryListenerCount();
        }

        @Override
        public Object getListener() {
            return this;
        }

        @Override
        public boolean eval(Object event) {
            return true;
        }
    }
}

