/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapRemovePartitionLostListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractRemoveListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class MapRemovePartitionLostListenerMessageTask
extends AbstractRemoveListenerMessageTask<MapRemovePartitionLostListenerCodec.RequestParameters> {
    public MapRemovePartitionLostListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean deRegisterListener() {
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        return mapService.getMapServiceContext().removePartitionLostListener(((MapRemovePartitionLostListenerCodec.RequestParameters)this.parameters).name, ((MapRemovePartitionLostListenerCodec.RequestParameters)this.parameters).registrationId);
    }

    @Override
    protected String getRegistrationId() {
        return ((MapRemovePartitionLostListenerCodec.RequestParameters)this.parameters).registrationId;
    }

    @Override
    protected MapRemovePartitionLostListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapRemovePartitionLostListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapRemovePartitionLostListenerCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapRemovePartitionLostListenerCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getMethodName() {
        return "removePartitionLostListener";
    }
}

