/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ContinuousQueryAddListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.core.IMapEvent;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.querycache.event.BatchEventData;
import com.hazelcast.map.impl.querycache.event.BatchIMapEvent;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.map.impl.querycache.event.SingleIMapEvent;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.impl.eventservice.impl.TrueEventFilter;
import java.security.Permission;

public class MapAddListenerMessageTask
extends AbstractCallableMessageTask<ContinuousQueryAddListenerCodec.RequestParameters>
implements ListenerAdapter<IMapEvent>,
ListenerMessageTask {
    public MapAddListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        return this.registerListener(this.endpoint, this);
    }

    private String registerListener(ClientEndpoint endpoint, ListenerAdapter adapter) {
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        String registrationId = ((ContinuousQueryAddListenerCodec.RequestParameters)this.parameters).localOnly ? mapServiceContext.addLocalListenerAdapter(adapter, ((ContinuousQueryAddListenerCodec.RequestParameters)this.parameters).listenerName) : mapServiceContext.addListenerAdapter(adapter, TrueEventFilter.INSTANCE, ((ContinuousQueryAddListenerCodec.RequestParameters)this.parameters).listenerName);
        endpoint.addListenerDestroyAction("hz:impl:mapService", ((ContinuousQueryAddListenerCodec.RequestParameters)this.parameters).listenerName, registrationId);
        return registrationId;
    }

    @Override
    public void onEvent(IMapEvent iMapEvent) {
        if (!this.endpoint.isAlive()) {
            return;
        }
        ClientMessage eventData = this.getEventData(iMapEvent);
        this.sendClientMessage(eventData);
    }

    private ClientMessage getEventData(IMapEvent iMapEvent) {
        if (iMapEvent instanceof SingleIMapEvent) {
            QueryCacheEventData eventData = ((SingleIMapEvent)iMapEvent).getEventData();
            ClientMessage clientMessage = ContinuousQueryAddListenerCodec.encodeQueryCacheSingleEvent(eventData);
            int partitionId = eventData.getPartitionId();
            clientMessage.setPartitionId(partitionId);
            return clientMessage;
        }
        if (iMapEvent instanceof BatchIMapEvent) {
            BatchIMapEvent batchIMapEvent = (BatchIMapEvent)iMapEvent;
            BatchEventData batchEventData = batchIMapEvent.getBatchEventData();
            int partitionId = batchEventData.getPartitionId();
            ClientMessage clientMessage = ContinuousQueryAddListenerCodec.encodeQueryCacheBatchEvent(batchEventData.getEvents(), batchEventData.getSource(), partitionId);
            clientMessage.setPartitionId(partitionId);
            return clientMessage;
        }
        throw new IllegalArgumentException("Unexpected event type found = [" + iMapEvent + "]");
    }

    @Override
    protected ContinuousQueryAddListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ContinuousQueryAddListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ContinuousQueryAddListenerCodec.encodeResponse((String)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((ContinuousQueryAddListenerCodec.RequestParameters)this.parameters).listenerName;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

