/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapPutIfAbsentCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPutMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.util.concurrent.TimeUnit;

public class MapPutIfAbsentMessageTask
extends AbstractMapPutMessageTask<MapPutIfAbsentCodec.RequestParameters> {
    public MapPutIfAbsentMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected MapPutIfAbsentCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapPutIfAbsentCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapPutIfAbsentCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapPutIfAbsentCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createPutIfAbsentOperation(((MapPutIfAbsentCodec.RequestParameters)this.parameters).name, ((MapPutIfAbsentCodec.RequestParameters)this.parameters).key, ((MapPutIfAbsentCodec.RequestParameters)this.parameters).value, ((MapPutIfAbsentCodec.RequestParameters)this.parameters).ttl, -1L);
        op.setThreadId(((MapPutIfAbsentCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapPutIfAbsentCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "putIfAbsent";
    }

    @Override
    public Object[] getParameters() {
        if (((MapPutIfAbsentCodec.RequestParameters)this.parameters).ttl == -1L) {
            return new Object[]{((MapPutIfAbsentCodec.RequestParameters)this.parameters).key, ((MapPutIfAbsentCodec.RequestParameters)this.parameters).value};
        }
        return new Object[]{((MapPutIfAbsentCodec.RequestParameters)this.parameters).key, ((MapPutIfAbsentCodec.RequestParameters)this.parameters).value, ((MapPutIfAbsentCodec.RequestParameters)this.parameters).ttl, TimeUnit.MILLISECONDS};
    }
}

