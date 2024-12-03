/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapAssignAndGetUuidsCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapAllPartitionsMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapAssignAndGetUuidsOperationFactory;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.Map;

public class MapAssignAndGetUuidsMessageTask
extends AbstractMapAllPartitionsMessageTask<MapAssignAndGetUuidsCodec.RequestParameters> {
    public MapAssignAndGetUuidsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected MapAssignAndGetUuidsCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapAssignAndGetUuidsCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapAssignAndGetUuidsCodec.encodeResponse(((Map)response).entrySet());
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }

    @Override
    protected OperationFactory createOperationFactory() {
        return new MapAssignAndGetUuidsOperationFactory();
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        return map;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }
}

