/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapEvictCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MapEvictMessageTask
extends AbstractMapPartitionMessageTask<MapEvictCodec.RequestParameters> {
    public MapEvictMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapEvictCodec.RequestParameters)this.parameters).name);
        MapOperation operation = operationProvider.createEvictOperation(((MapEvictCodec.RequestParameters)this.parameters).name, ((MapEvictCodec.RequestParameters)this.parameters).key, false);
        operation.setThreadId(((MapEvictCodec.RequestParameters)this.parameters).threadId);
        return operation;
    }

    @Override
    protected MapEvictCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapEvictCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapEvictCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapEvictCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapEvictCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "evict";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapEvictCodec.RequestParameters)this.parameters).key};
    }
}

