/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapDeleteCodec;
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

public class MapDeleteMessageTask
extends AbstractMapPartitionMessageTask<MapDeleteCodec.RequestParameters> {
    private transient long startTimeNanos;

    public MapDeleteMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapDeleteCodec.RequestParameters)this.parameters).name);
        MapOperation op = operationProvider.createDeleteOperation(((MapDeleteCodec.RequestParameters)this.parameters).name, ((MapDeleteCodec.RequestParameters)this.parameters).key, false);
        op.setThreadId(((MapDeleteCodec.RequestParameters)this.parameters).threadId);
        return op;
    }

    @Override
    protected void beforeProcess() {
        this.startTimeNanos = System.nanoTime();
    }

    @Override
    protected void beforeResponse() {
        long latencyNanos = System.nanoTime() - this.startTimeNanos;
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        MapContainer mapContainer = mapService.getMapServiceContext().getMapContainer(((MapDeleteCodec.RequestParameters)this.parameters).name);
        if (mapContainer.getMapConfig().isStatisticsEnabled()) {
            mapService.getMapServiceContext().getLocalMapStatsProvider().getLocalMapStatsImpl(((MapDeleteCodec.RequestParameters)this.parameters).name).incrementRemoveLatencyNanos(latencyNanos);
        }
    }

    @Override
    protected MapDeleteCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapDeleteCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapDeleteCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapDeleteCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapDeleteCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "delete";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapDeleteCodec.RequestParameters)this.parameters).key};
    }
}

