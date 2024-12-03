/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapPutAllCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.MapUtil;
import java.security.Permission;
import java.util.Map;

public class MapPutAllMessageTask
extends AbstractMapPartitionMessageTask<MapPutAllCodec.RequestParameters> {
    private volatile long startTimeNanos;

    public MapPutAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        MapEntries mapEntries = new MapEntries(((MapPutAllCodec.RequestParameters)this.parameters).entries);
        MapOperationProvider operationProvider = this.getMapOperationProvider(((MapPutAllCodec.RequestParameters)this.parameters).name);
        return operationProvider.createPutAllOperation(((MapPutAllCodec.RequestParameters)this.parameters).name, mapEntries);
    }

    @Override
    protected MapPutAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapPutAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapPutAllCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    protected void beforeProcess() {
        this.startTimeNanos = System.nanoTime();
    }

    @Override
    protected void beforeResponse() {
        long latencyNanos = System.nanoTime() - this.startTimeNanos;
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        MapContainer mapContainer = mapService.getMapServiceContext().getMapContainer(((MapPutAllCodec.RequestParameters)this.parameters).name);
        if (mapContainer.getMapConfig().isStatisticsEnabled()) {
            mapService.getMapServiceContext().getLocalMapStatsProvider().getLocalMapStatsImpl(((MapPutAllCodec.RequestParameters)this.parameters).name).incrementPutLatencyNanos(((MapPutAllCodec.RequestParameters)this.parameters).entries.size(), latencyNanos);
        }
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapPutAllCodec.RequestParameters)this.parameters).name, "put");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapPutAllCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "putAll";
    }

    @Override
    public Object[] getParameters() {
        Map<Data, Data> map = MapUtil.createHashMap(((MapPutAllCodec.RequestParameters)this.parameters).entries.size());
        for (Map.Entry<Data, Data> entry : ((MapPutAllCodec.RequestParameters)this.parameters).entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return new Object[]{map};
    }
}

