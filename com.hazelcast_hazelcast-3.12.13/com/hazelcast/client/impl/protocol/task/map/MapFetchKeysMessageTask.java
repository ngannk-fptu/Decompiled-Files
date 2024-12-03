/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapFetchKeysCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.iterator.MapKeysWithCursor;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.Collections;

public class MapFetchKeysMessageTask
extends AbstractMapPartitionMessageTask<MapFetchKeysCodec.RequestParameters> {
    public MapFetchKeysMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapFetchKeysCodec.RequestParameters)this.parameters).name);
        return operationProvider.createFetchKeysOperation(((MapFetchKeysCodec.RequestParameters)this.parameters).name, ((MapFetchKeysCodec.RequestParameters)this.parameters).tableIndex, ((MapFetchKeysCodec.RequestParameters)this.parameters).batch);
    }

    @Override
    protected MapFetchKeysCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapFetchKeysCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        if (response == null) {
            return MapFetchKeysCodec.encodeResponse(0, Collections.emptyList());
        }
        MapKeysWithCursor mapKeysWithCursor = (MapKeysWithCursor)response;
        return MapFetchKeysCodec.encodeResponse(mapKeysWithCursor.getNextTableIndexToReadFrom(), mapKeysWithCursor.getBatch());
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapFetchKeysCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "iterator";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}

