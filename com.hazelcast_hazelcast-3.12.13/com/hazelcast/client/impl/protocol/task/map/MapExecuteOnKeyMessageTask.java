/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapExecuteOnKeyCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MapExecuteOnKeyMessageTask
extends AbstractMapPartitionMessageTask<MapExecuteOnKeyCodec.RequestParameters> {
    public MapExecuteOnKeyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        EntryProcessor processor = (EntryProcessor)this.serializationService.toObject(((MapExecuteOnKeyCodec.RequestParameters)this.parameters).entryProcessor);
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapExecuteOnKeyCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createEntryOperation(((MapExecuteOnKeyCodec.RequestParameters)this.parameters).name, ((MapExecuteOnKeyCodec.RequestParameters)this.parameters).key, processor);
        op.setThreadId(((MapExecuteOnKeyCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    protected MapExecuteOnKeyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapExecuteOnKeyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapExecuteOnKeyCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapExecuteOnKeyCodec.RequestParameters)this.parameters).name, "put", "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapExecuteOnKeyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "executeOnKey";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapExecuteOnKeyCodec.RequestParameters)this.parameters).key, ((MapExecuteOnKeyCodec.RequestParameters)this.parameters).entryProcessor};
    }
}

