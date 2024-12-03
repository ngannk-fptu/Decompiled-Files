/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapPutIfAbsentWithMaxIdleCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPutWithMaxIdleMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.util.concurrent.TimeUnit;

public class MapPutIfAbsentWithMaxIdleMessageTask
extends AbstractMapPutWithMaxIdleMessageTask<MapPutIfAbsentWithMaxIdleCodec.RequestParameters> {
    public MapPutIfAbsentWithMaxIdleMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected MapPutIfAbsentWithMaxIdleCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapPutIfAbsentWithMaxIdleCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapPutIfAbsentWithMaxIdleCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapPutIfAbsentWithMaxIdleCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createPutIfAbsentOperation(((MapPutIfAbsentWithMaxIdleCodec.RequestParameters)this.parameters).name, ((MapPutIfAbsentWithMaxIdleCodec.RequestParameters)this.parameters).key, ((MapPutIfAbsentWithMaxIdleCodec.RequestParameters)this.parameters).value, ((MapPutIfAbsentWithMaxIdleCodec.RequestParameters)this.parameters).ttl, ((MapPutIfAbsentWithMaxIdleCodec.RequestParameters)this.parameters).maxIdle);
        op.setThreadId(((MapPutIfAbsentWithMaxIdleCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapPutIfAbsentWithMaxIdleCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "putIfAbsent";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapPutIfAbsentWithMaxIdleCodec.RequestParameters)this.parameters).key, ((MapPutIfAbsentWithMaxIdleCodec.RequestParameters)this.parameters).value, ((MapPutIfAbsentWithMaxIdleCodec.RequestParameters)this.parameters).ttl, TimeUnit.MILLISECONDS, ((MapPutIfAbsentWithMaxIdleCodec.RequestParameters)this.parameters).maxIdle, TimeUnit.MILLISECONDS};
    }
}

