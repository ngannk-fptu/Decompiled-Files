/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapRemoveCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MapRemoveMessageTask
extends AbstractMapPartitionMessageTask<MapRemoveCodec.RequestParameters> {
    protected transient long startTimeNanos;

    public MapRemoveMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void beforeProcess() {
        this.startTimeNanos = System.nanoTime();
    }

    @Override
    protected void beforeResponse() {
        long latencyNanos = System.nanoTime() - this.startTimeNanos;
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        MapContainer mapContainer = mapService.getMapServiceContext().getMapContainer(((MapRemoveCodec.RequestParameters)this.parameters).name);
        if (mapContainer.getMapConfig().isStatisticsEnabled()) {
            mapService.getMapServiceContext().getLocalMapStatsProvider().getLocalMapStatsImpl(((MapRemoveCodec.RequestParameters)this.parameters).name).incrementRemoveLatencyNanos(latencyNanos);
        }
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapRemoveCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createRemoveOperation(((MapRemoveCodec.RequestParameters)this.parameters).name, ((MapRemoveCodec.RequestParameters)this.parameters).key, false);
        op.setThreadId(((MapRemoveCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    protected MapRemoveCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapRemoveCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapRemoveCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapRemoveCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapRemoveCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "remove";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapRemoveCodec.RequestParameters)this.parameters).key};
    }
}

