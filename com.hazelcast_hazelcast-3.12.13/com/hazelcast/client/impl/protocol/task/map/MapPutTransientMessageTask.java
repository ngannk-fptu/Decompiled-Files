/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapPutTransientCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPutMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.util.concurrent.TimeUnit;

public class MapPutTransientMessageTask
extends AbstractMapPutMessageTask<MapPutTransientCodec.RequestParameters> {
    public MapPutTransientMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapPutTransientCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createPutTransientOperation(((MapPutTransientCodec.RequestParameters)this.parameters).name, ((MapPutTransientCodec.RequestParameters)this.parameters).key, ((MapPutTransientCodec.RequestParameters)this.parameters).value, ((MapPutTransientCodec.RequestParameters)this.parameters).ttl, -1L);
        op.setThreadId(((MapPutTransientCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    protected MapPutTransientCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapPutTransientCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapPutTransientCodec.encodeResponse();
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapPutTransientCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "putTransient";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapPutTransientCodec.RequestParameters)this.parameters).key, ((MapPutTransientCodec.RequestParameters)this.parameters).value, ((MapPutTransientCodec.RequestParameters)this.parameters).ttl, TimeUnit.MILLISECONDS};
    }
}

