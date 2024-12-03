/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapGetAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.operation.GetAllOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MapGetAllMessageTask
extends AbstractPartitionMessageTask<MapGetAllCodec.RequestParameters> {
    private volatile long startTimeNanos;

    public MapGetAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new GetAllOperation(((MapGetAllCodec.RequestParameters)this.parameters).name, ((MapGetAllCodec.RequestParameters)this.parameters).keys);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    protected MapGetAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapGetAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected void beforeProcess() {
        this.startTimeNanos = System.nanoTime();
    }

    @Override
    protected void beforeResponse() {
        long latencyNanos = System.nanoTime() - this.startTimeNanos;
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        MapContainer mapContainer = mapService.getMapServiceContext().getMapContainer(((MapGetAllCodec.RequestParameters)this.parameters).name);
        if (mapContainer.getMapConfig().isStatisticsEnabled()) {
            mapService.getMapServiceContext().getLocalMapStatsProvider().getLocalMapStatsImpl(((MapGetAllCodec.RequestParameters)this.parameters).name).incrementGetLatencyNanos(((MapGetAllCodec.RequestParameters)this.parameters).keys.size(), latencyNanos);
        }
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapGetAllCodec.encodeResponse(((MapEntries)response).entries());
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapGetAllCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapGetAllCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getAll";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapGetAllCodec.RequestParameters)this.parameters).keys};
    }
}

