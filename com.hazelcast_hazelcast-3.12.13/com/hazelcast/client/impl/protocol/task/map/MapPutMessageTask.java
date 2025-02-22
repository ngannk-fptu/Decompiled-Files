/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapPutCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPutMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.util.concurrent.TimeUnit;

public class MapPutMessageTask
extends AbstractMapPutMessageTask<MapPutCodec.RequestParameters> {
    public MapPutMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapPutCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createPutOperation(((MapPutCodec.RequestParameters)this.parameters).name, ((MapPutCodec.RequestParameters)this.parameters).key, ((MapPutCodec.RequestParameters)this.parameters).value, ((MapPutCodec.RequestParameters)this.parameters).ttl, -1L);
        op.setThreadId(((MapPutCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    protected MapPutCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapPutCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapPutCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapPutCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "put";
    }

    @Override
    public Object[] getParameters() {
        if (((MapPutCodec.RequestParameters)this.parameters).ttl == -1L) {
            return new Object[]{((MapPutCodec.RequestParameters)this.parameters).key, ((MapPutCodec.RequestParameters)this.parameters).value};
        }
        return new Object[]{((MapPutCodec.RequestParameters)this.parameters).key, ((MapPutCodec.RequestParameters)this.parameters).value, ((MapPutCodec.RequestParameters)this.parameters).ttl, TimeUnit.MILLISECONDS};
    }
}

