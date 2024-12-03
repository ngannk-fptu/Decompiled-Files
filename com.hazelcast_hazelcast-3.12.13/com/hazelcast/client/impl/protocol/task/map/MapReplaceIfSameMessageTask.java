/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapReplaceIfSameCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPutMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;

public class MapReplaceIfSameMessageTask
extends AbstractMapPutMessageTask<MapReplaceIfSameCodec.RequestParameters> {
    public MapReplaceIfSameMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapReplaceIfSameCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createReplaceIfSameOperation(((MapReplaceIfSameCodec.RequestParameters)this.parameters).name, ((MapReplaceIfSameCodec.RequestParameters)this.parameters).key, ((MapReplaceIfSameCodec.RequestParameters)this.parameters).testValue, ((MapReplaceIfSameCodec.RequestParameters)this.parameters).value);
        op.setThreadId(((MapReplaceIfSameCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    protected MapReplaceIfSameCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapReplaceIfSameCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapReplaceIfSameCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapReplaceIfSameCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "replace";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapReplaceIfSameCodec.RequestParameters)this.parameters).key, ((MapReplaceIfSameCodec.RequestParameters)this.parameters).testValue, ((MapReplaceIfSameCodec.RequestParameters)this.parameters).value};
    }
}

