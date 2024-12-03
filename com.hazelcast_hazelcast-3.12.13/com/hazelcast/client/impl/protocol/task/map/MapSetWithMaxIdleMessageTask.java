/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapSetWithMaxIdleCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPutWithMaxIdleMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.util.concurrent.TimeUnit;

public class MapSetWithMaxIdleMessageTask
extends AbstractMapPutWithMaxIdleMessageTask<MapSetWithMaxIdleCodec.RequestParameters> {
    public MapSetWithMaxIdleMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected MapSetWithMaxIdleCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapSetWithMaxIdleCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapSetWithMaxIdleCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapSetWithMaxIdleCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createSetOperation(((MapSetWithMaxIdleCodec.RequestParameters)this.parameters).name, ((MapSetWithMaxIdleCodec.RequestParameters)this.parameters).key, ((MapSetWithMaxIdleCodec.RequestParameters)this.parameters).value, ((MapSetWithMaxIdleCodec.RequestParameters)this.parameters).ttl, ((MapSetWithMaxIdleCodec.RequestParameters)this.parameters).maxIdle);
        op.setThreadId(((MapSetWithMaxIdleCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapSetWithMaxIdleCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "set";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapSetWithMaxIdleCodec.RequestParameters)this.parameters).key, ((MapSetWithMaxIdleCodec.RequestParameters)this.parameters).value, ((MapSetWithMaxIdleCodec.RequestParameters)this.parameters).ttl, TimeUnit.MILLISECONDS, ((MapSetWithMaxIdleCodec.RequestParameters)this.parameters).maxIdle, TimeUnit.MILLISECONDS};
    }
}

