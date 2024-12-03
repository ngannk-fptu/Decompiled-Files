/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapTryRemoveCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class MapTryRemoveMessageTask
extends AbstractMapPartitionMessageTask<MapTryRemoveCodec.RequestParameters> {
    public MapTryRemoveMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected MapTryRemoveCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapTryRemoveCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapTryRemoveCodec.encodeResponse((Boolean)response);
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapTryRemoveCodec.RequestParameters)this.parameters).name);
        MapOperation operation = operationProvider.createTryRemoveOperation(((MapTryRemoveCodec.RequestParameters)this.parameters).name, ((MapTryRemoveCodec.RequestParameters)this.parameters).key, ((MapTryRemoveCodec.RequestParameters)this.parameters).timeout);
        operation.setThreadId(((MapTryRemoveCodec.RequestParameters)this.parameters).threadId);
        return operation;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapTryRemoveCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapTryRemoveCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "tryRemove";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapTryRemoveCodec.RequestParameters)this.parameters).key, ((MapTryRemoveCodec.RequestParameters)this.parameters).timeout, TimeUnit.MILLISECONDS};
    }
}

