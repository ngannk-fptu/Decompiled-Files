/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapContainsKeyCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.LocalMapStatsProvider;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MapContainsKeyMessageTask
extends AbstractMapPartitionMessageTask<MapContainsKeyCodec.RequestParameters> {
    public MapContainsKeyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        MapOperation operation = this.getMapOperationProvider(((MapContainsKeyCodec.RequestParameters)this.parameters).name).createContainsKeyOperation(((MapContainsKeyCodec.RequestParameters)this.parameters).name, ((MapContainsKeyCodec.RequestParameters)this.parameters).key);
        operation.setThreadId(((MapContainsKeyCodec.RequestParameters)this.parameters).threadId);
        return operation;
    }

    @Override
    protected void afterResponse() {
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        MapContainer mapContainer = mapService.getMapServiceContext().getMapContainer(((MapContainsKeyCodec.RequestParameters)this.parameters).name);
        if (mapContainer.getMapConfig().isStatisticsEnabled()) {
            LocalMapStatsProvider localMapStatsProvider = mapService.getMapServiceContext().getLocalMapStatsProvider();
            localMapStatsProvider.getLocalMapStatsImpl(((MapContainsKeyCodec.RequestParameters)this.parameters).name).incrementOtherOperations();
        }
    }

    @Override
    protected MapContainsKeyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapContainsKeyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapContainsKeyCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapContainsKeyCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapContainsKeyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "containsKey";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapContainsKeyCodec.RequestParameters)this.parameters).key};
    }
}

