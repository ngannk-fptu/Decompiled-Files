/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapRemoveIfSameCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MapRemoveIfSameMessageTask
extends AbstractMapPartitionMessageTask<MapRemoveIfSameCodec.RequestParameters> {
    public MapRemoveIfSameMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapRemoveIfSameCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createRemoveIfSameOperation(((MapRemoveIfSameCodec.RequestParameters)this.parameters).name, ((MapRemoveIfSameCodec.RequestParameters)this.parameters).key, ((MapRemoveIfSameCodec.RequestParameters)this.parameters).value);
        op.setThreadId(((MapRemoveIfSameCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    protected MapRemoveIfSameCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapRemoveIfSameCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapRemoveIfSameCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapRemoveIfSameCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapRemoveIfSameCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "remove";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapRemoveIfSameCodec.RequestParameters)this.parameters).key, ((MapRemoveIfSameCodec.RequestParameters)this.parameters).value};
    }
}

