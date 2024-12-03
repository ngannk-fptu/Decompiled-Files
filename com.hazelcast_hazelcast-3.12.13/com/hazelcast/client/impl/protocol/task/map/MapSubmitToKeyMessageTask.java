/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapSubmitToKeyCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MapSubmitToKeyMessageTask
extends AbstractMapPartitionMessageTask<MapSubmitToKeyCodec.RequestParameters> {
    public MapSubmitToKeyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        EntryProcessor processor = (EntryProcessor)this.serializationService.toObject(((MapSubmitToKeyCodec.RequestParameters)this.parameters).entryProcessor);
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapSubmitToKeyCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createEntryOperation(((MapSubmitToKeyCodec.RequestParameters)this.parameters).name, ((MapSubmitToKeyCodec.RequestParameters)this.parameters).key, processor);
        op.setThreadId(((MapSubmitToKeyCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    protected MapSubmitToKeyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapSubmitToKeyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapSubmitToKeyCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapSubmitToKeyCodec.RequestParameters)this.parameters).name, "put", "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapSubmitToKeyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "submitToKey";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapSubmitToKeyCodec.RequestParameters)this.parameters).key, ((MapSubmitToKeyCodec.RequestParameters)this.parameters).entryProcessor};
    }
}

