/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapFetchWithQueryCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.QueryResultRow;
import com.hazelcast.map.impl.query.ResultSegment;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.projection.Projection;
import com.hazelcast.query.Predicate;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.IterationType;
import java.security.Permission;
import java.util.ArrayList;

public class MapFetchWithQueryMessageTask
extends AbstractMapPartitionMessageTask<MapFetchWithQueryCodec.RequestParameters> {
    public MapFetchWithQueryMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapFetchWithQueryCodec.RequestParameters)this.parameters).name);
        Projection projection = (Projection)this.nodeEngine.getSerializationService().toObject(((MapFetchWithQueryCodec.RequestParameters)this.parameters).projection);
        Predicate predicate = (Predicate)this.nodeEngine.getSerializationService().toObject(((MapFetchWithQueryCodec.RequestParameters)this.parameters).predicate);
        Query query = Query.of().mapName(((MapFetchWithQueryCodec.RequestParameters)this.parameters).name).iterationType(IterationType.VALUE).predicate(predicate).projection(projection).build();
        return operationProvider.createFetchWithQueryOperation(((MapFetchWithQueryCodec.RequestParameters)this.parameters).name, ((MapFetchWithQueryCodec.RequestParameters)this.parameters).tableIndex, ((MapFetchWithQueryCodec.RequestParameters)this.parameters).batch, query);
    }

    @Override
    protected MapFetchWithQueryCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapFetchWithQueryCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        ResultSegment resp = (ResultSegment)response;
        QueryResult queryResult = (QueryResult)resp.getResult();
        ArrayList<Data> serialized = new ArrayList<Data>(queryResult.size());
        for (QueryResultRow row : queryResult) {
            serialized.add(row.getValue());
        }
        return MapFetchWithQueryCodec.encodeResponse(serialized, resp.getNextTableIndexToReadFrom());
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapFetchWithQueryCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapFetchWithQueryCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "iterator";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapFetchWithQueryCodec.RequestParameters)this.parameters).batch, this.getPartitionId(), ((MapFetchWithQueryCodec.RequestParameters)this.parameters).projection, ((MapFetchWithQueryCodec.RequestParameters)this.parameters).predicate};
    }
}

