/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapSetCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPutMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.util.concurrent.TimeUnit;

public class MapSetMessageTask
extends AbstractMapPutMessageTask<MapSetCodec.RequestParameters> {
    public MapSetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapSetCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createSetOperation(((MapSetCodec.RequestParameters)this.parameters).name, ((MapSetCodec.RequestParameters)this.parameters).key, ((MapSetCodec.RequestParameters)this.parameters).value, ((MapSetCodec.RequestParameters)this.parameters).ttl, -1L);
        op.setThreadId(((MapSetCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    protected MapSetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapSetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapSetCodec.encodeResponse();
    }

    @Override
    public String getMethodName() {
        return "set";
    }

    @Override
    public Object[] getParameters() {
        if (((MapSetCodec.RequestParameters)this.parameters).ttl == -1L) {
            return new Object[]{((MapSetCodec.RequestParameters)this.parameters).key, ((MapSetCodec.RequestParameters)this.parameters).value};
        }
        return new Object[]{((MapSetCodec.RequestParameters)this.parameters).key, ((MapSetCodec.RequestParameters)this.parameters).value, ((MapSetCodec.RequestParameters)this.parameters).ttl, TimeUnit.MILLISECONDS};
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapSetCodec.RequestParameters)this.parameters).name;
    }
}

