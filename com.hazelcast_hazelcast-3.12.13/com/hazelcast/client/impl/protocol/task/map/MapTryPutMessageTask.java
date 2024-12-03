/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapTryPutCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPutMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.util.concurrent.TimeUnit;

public class MapTryPutMessageTask
extends AbstractMapPutMessageTask<MapTryPutCodec.RequestParameters> {
    public MapTryPutMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapTryPutCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createTryPutOperation(((MapTryPutCodec.RequestParameters)this.parameters).name, ((MapTryPutCodec.RequestParameters)this.parameters).key, ((MapTryPutCodec.RequestParameters)this.parameters).value, ((MapTryPutCodec.RequestParameters)this.parameters).timeout);
        op.setThreadId(((MapTryPutCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapTryPutCodec.encodeResponse((Boolean)response);
    }

    @Override
    protected MapTryPutCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapTryPutCodec.decodeRequest(clientMessage);
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapTryPutCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "tryPut";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapTryPutCodec.RequestParameters)this.parameters).key, ((MapTryPutCodec.RequestParameters)this.parameters).value, ((MapTryPutCodec.RequestParameters)this.parameters).timeout, TimeUnit.MILLISECONDS};
    }
}

