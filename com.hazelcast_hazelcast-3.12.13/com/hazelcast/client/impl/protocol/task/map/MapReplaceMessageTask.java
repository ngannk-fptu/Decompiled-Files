/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapReplaceCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPutMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;

public class MapReplaceMessageTask
extends AbstractMapPutMessageTask<MapReplaceCodec.RequestParameters> {
    public MapReplaceMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapReplaceCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createReplaceOperation(((MapReplaceCodec.RequestParameters)this.parameters).name, ((MapReplaceCodec.RequestParameters)this.parameters).key, ((MapReplaceCodec.RequestParameters)this.parameters).value);
        op.setThreadId(((MapReplaceCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    protected MapReplaceCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapReplaceCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapReplaceCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapReplaceCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "replace";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapReplaceCodec.RequestParameters)this.parameters).key, ((MapReplaceCodec.RequestParameters)this.parameters).value};
    }
}

