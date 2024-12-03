/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapAddPartitionLostListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.MapPartitionLostEvent;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.listener.MapPartitionLostListener;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import java.security.Permission;

public class MapAddPartitionLostListenerMessageTask
extends AbstractCallableMessageTask<MapAddPartitionLostListenerCodec.RequestParameters>
implements ListenerMessageTask {
    public MapAddPartitionLostListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() {
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        MapPartitionLostListener listener = new MapPartitionLostListener(){

            @Override
            public void partitionLost(MapPartitionLostEvent event) {
                if (MapAddPartitionLostListenerMessageTask.this.endpoint.isAlive()) {
                    ClientMessage eventMessage = MapAddPartitionLostListenerCodec.encodeMapPartitionLostEvent(event.getPartitionId(), event.getMember().getUuid());
                    MapAddPartitionLostListenerMessageTask.this.sendClientMessage(null, eventMessage);
                }
            }
        };
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        String registrationId = ((MapAddPartitionLostListenerCodec.RequestParameters)this.parameters).localOnly ? mapServiceContext.addLocalPartitionLostListener(listener, ((MapAddPartitionLostListenerCodec.RequestParameters)this.parameters).name) : mapServiceContext.addPartitionLostListener(listener, ((MapAddPartitionLostListenerCodec.RequestParameters)this.parameters).name);
        this.endpoint.addListenerDestroyAction("hz:impl:mapService", ((MapAddPartitionLostListenerCodec.RequestParameters)this.parameters).name, registrationId);
        return registrationId;
    }

    @Override
    protected MapAddPartitionLostListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapAddPartitionLostListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapAddPartitionLostListenerCodec.encodeResponse((String)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public String getMethodName() {
        return "addPartitionLostListener";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null};
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapAddPartitionLostListenerCodec.RequestParameters)this.parameters).name, "listen");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapAddPartitionLostListenerCodec.RequestParameters)this.parameters).name;
    }
}

