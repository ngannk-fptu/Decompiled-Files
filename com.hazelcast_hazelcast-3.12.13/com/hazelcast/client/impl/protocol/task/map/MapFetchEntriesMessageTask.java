/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapFetchEntriesCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.iterator.MapEntriesWithCursor;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.Collections;

public class MapFetchEntriesMessageTask
extends AbstractMapPartitionMessageTask<MapFetchEntriesCodec.RequestParameters> {
    public MapFetchEntriesMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapFetchEntriesCodec.RequestParameters)this.parameters).name);
        return operationProvider.createFetchEntriesOperation(((MapFetchEntriesCodec.RequestParameters)this.parameters).name, ((MapFetchEntriesCodec.RequestParameters)this.parameters).tableIndex, ((MapFetchEntriesCodec.RequestParameters)this.parameters).batch);
    }

    @Override
    protected MapFetchEntriesCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapFetchEntriesCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        if (response == null) {
            return MapFetchEntriesCodec.encodeResponse(0, Collections.emptyList());
        }
        MapEntriesWithCursor mapEntriesWithCursor = (MapEntriesWithCursor)response;
        return MapFetchEntriesCodec.encodeResponse(mapEntriesWithCursor.getNextTableIndexToReadFrom(), mapEntriesWithCursor.getBatch());
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
        return ((MapFetchEntriesCodec.RequestParameters)this.parameters).name;
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

