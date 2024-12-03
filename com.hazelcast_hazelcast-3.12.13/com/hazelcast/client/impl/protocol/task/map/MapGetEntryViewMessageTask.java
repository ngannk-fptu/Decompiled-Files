/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapGetEntryViewCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.SimpleEntryView;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MapGetEntryViewMessageTask
extends AbstractMapPartitionMessageTask<MapGetEntryViewCodec.RequestParameters> {
    public MapGetEntryViewMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapGetEntryViewCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createGetEntryViewOperation(((MapGetEntryViewCodec.RequestParameters)this.parameters).name, ((MapGetEntryViewCodec.RequestParameters)this.parameters).key);
        op.setThreadId(((MapGetEntryViewCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    protected MapGetEntryViewCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapGetEntryViewCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        SimpleEntryView dataEntryView;
        return MapGetEntryViewCodec.encodeResponse(dataEntryView, (dataEntryView = (SimpleEntryView)response) != null ? dataEntryView.getMaxIdle() : 0L);
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapGetEntryViewCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapGetEntryViewCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getEntryView";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapGetEntryViewCodec.RequestParameters)this.parameters).key};
    }
}

