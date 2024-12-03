/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.cache;

import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.event.CachePartitionLostEvent;
import com.hazelcast.cache.impl.event.CachePartitionLostEventFilter;
import com.hazelcast.cache.impl.event.CachePartitionLostListener;
import com.hazelcast.cache.impl.event.InternalCachePartitionLostListenerAdapter;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CacheAddPartitionLostListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import java.security.Permission;

public class CacheAddPartitionLostListenerMessageTask
extends AbstractCallableMessageTask<CacheAddPartitionLostListenerCodec.RequestParameters>
implements ListenerMessageTask {
    public CacheAddPartitionLostListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() {
        CachePartitionLostListener listener = new CachePartitionLostListener(){

            @Override
            public void partitionLost(CachePartitionLostEvent event) {
                if (CacheAddPartitionLostListenerMessageTask.this.endpoint.isAlive()) {
                    ClientMessage eventMessage = CacheAddPartitionLostListenerCodec.encodeCachePartitionLostEvent(event.getPartitionId(), event.getMember().getUuid());
                    CacheAddPartitionLostListenerMessageTask.this.sendClientMessage(null, eventMessage);
                }
            }
        };
        InternalCachePartitionLostListenerAdapter listenerAdapter = new InternalCachePartitionLostListenerAdapter(listener);
        CachePartitionLostEventFilter filter = new CachePartitionLostEventFilter();
        CacheService service = (CacheService)this.getService("hz:impl:cacheService");
        EventService eventService = service.getNodeEngine().getEventService();
        EventRegistration registration = ((CacheAddPartitionLostListenerCodec.RequestParameters)this.parameters).localOnly ? eventService.registerLocalListener("hz:impl:cacheService", ((CacheAddPartitionLostListenerCodec.RequestParameters)this.parameters).name, filter, listenerAdapter) : eventService.registerListener("hz:impl:cacheService", ((CacheAddPartitionLostListenerCodec.RequestParameters)this.parameters).name, filter, listenerAdapter);
        String registrationId = registration.getId();
        this.endpoint.addListenerDestroyAction("hz:impl:cacheService", ((CacheAddPartitionLostListenerCodec.RequestParameters)this.parameters).name, registrationId);
        return registrationId;
    }

    @Override
    protected CacheAddPartitionLostListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CacheAddPartitionLostListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CacheAddPartitionLostListenerCodec.encodeResponse((String)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public String getMethodName() {
        return "addCachePartitionLostListener";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((CacheAddPartitionLostListenerCodec.RequestParameters)this.parameters).name;
    }
}

