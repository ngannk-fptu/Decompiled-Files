/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapPutTransientWithMaxIdleCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPutWithMaxIdleMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.util.concurrent.TimeUnit;

public class MapPutTransientWithMaxIdleMessageTask
extends AbstractMapPutWithMaxIdleMessageTask<MapPutTransientWithMaxIdleCodec.RequestParameters> {
    public MapPutTransientWithMaxIdleMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected MapPutTransientWithMaxIdleCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapPutTransientWithMaxIdleCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapPutTransientWithMaxIdleCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapPutTransientWithMaxIdleCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createPutTransientOperation(((MapPutTransientWithMaxIdleCodec.RequestParameters)this.parameters).name, ((MapPutTransientWithMaxIdleCodec.RequestParameters)this.parameters).key, ((MapPutTransientWithMaxIdleCodec.RequestParameters)this.parameters).value, ((MapPutTransientWithMaxIdleCodec.RequestParameters)this.parameters).ttl, ((MapPutTransientWithMaxIdleCodec.RequestParameters)this.parameters).maxIdle);
        op.setThreadId(((MapPutTransientWithMaxIdleCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapPutTransientWithMaxIdleCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "putTransient";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapPutTransientWithMaxIdleCodec.RequestParameters)this.parameters).key, ((MapPutTransientWithMaxIdleCodec.RequestParameters)this.parameters).value, ((MapPutTransientWithMaxIdleCodec.RequestParameters)this.parameters).ttl, TimeUnit.MILLISECONDS, ((MapPutTransientWithMaxIdleCodec.RequestParameters)this.parameters).maxIdle, TimeUnit.MILLISECONDS};
    }
}

