/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapPutWithMaxIdleCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPutWithMaxIdleMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.util.concurrent.TimeUnit;

public class MapPutWithMaxIdleMessageTask
extends AbstractMapPutWithMaxIdleMessageTask<MapPutWithMaxIdleCodec.RequestParameters> {
    public MapPutWithMaxIdleMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected MapPutWithMaxIdleCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapPutWithMaxIdleCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapPutWithMaxIdleCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapPutWithMaxIdleCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createPutOperation(((MapPutWithMaxIdleCodec.RequestParameters)this.parameters).name, ((MapPutWithMaxIdleCodec.RequestParameters)this.parameters).key, ((MapPutWithMaxIdleCodec.RequestParameters)this.parameters).value, ((MapPutWithMaxIdleCodec.RequestParameters)this.parameters).ttl, ((MapPutWithMaxIdleCodec.RequestParameters)this.parameters).maxIdle);
        op.setThreadId(((MapPutWithMaxIdleCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapPutWithMaxIdleCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "put";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapPutWithMaxIdleCodec.RequestParameters)this.parameters).key, ((MapPutWithMaxIdleCodec.RequestParameters)this.parameters).value, ((MapPutWithMaxIdleCodec.RequestParameters)this.parameters).ttl, TimeUnit.MILLISECONDS, ((MapPutWithMaxIdleCodec.RequestParameters)this.parameters).maxIdle, TimeUnit.MILLISECONDS};
    }
}

