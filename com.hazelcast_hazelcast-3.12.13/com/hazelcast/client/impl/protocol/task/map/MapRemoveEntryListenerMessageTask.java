/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapRemoveEntryListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractRemoveListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import java.security.Permission;

public class MapRemoveEntryListenerMessageTask
extends AbstractRemoveListenerMessageTask<MapRemoveEntryListenerCodec.RequestParameters> {
    public MapRemoveEntryListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean deRegisterListener() {
        MapService service = (MapService)this.getService("hz:impl:mapService");
        return service.getMapServiceContext().removeEventListener(((MapRemoveEntryListenerCodec.RequestParameters)this.parameters).name, ((MapRemoveEntryListenerCodec.RequestParameters)this.parameters).registrationId);
    }

    @Override
    protected String getRegistrationId() {
        return ((MapRemoveEntryListenerCodec.RequestParameters)this.parameters).registrationId;
    }

    @Override
    protected MapRemoveEntryListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapRemoveEntryListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapRemoveEntryListenerCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapRemoveEntryListenerCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapRemoveEntryListenerCodec.RequestParameters)this.parameters).name, "listen");
    }

    @Override
    public String getMethodName() {
        return "removeEntryListener";
    }
}

