/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapClearCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapAllPartitionsMessageTask;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.LocalMapStatsProvider;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.event.MapEventPublisher;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.Map;

public class MapClearMessageTask
extends AbstractMapAllPartitionsMessageTask<MapClearCodec.RequestParameters> {
    public MapClearMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        MapOperationProvider operationProvider = this.getOperationProvider(((MapClearCodec.RequestParameters)this.parameters).name);
        return operationProvider.createClearOperationFactory(((MapClearCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        MapService mapService;
        MapContainer mapContainer;
        int clearedTotal = 0;
        for (Object affectedEntries : map.values()) {
            clearedTotal += ((Integer)affectedEntries).intValue();
        }
        MapService service = (MapService)this.getService("hz:impl:mapService");
        MapServiceContext mapServiceContext = service.getMapServiceContext();
        if (clearedTotal > 0) {
            Address thisAddress = this.nodeEngine.getThisAddress();
            MapEventPublisher mapEventPublisher = mapServiceContext.getMapEventPublisher();
            mapEventPublisher.publishMapEvent(thisAddress, ((MapClearCodec.RequestParameters)this.parameters).name, EntryEventType.CLEAR_ALL, clearedTotal);
        }
        if ((mapContainer = (mapService = (MapService)this.getService("hz:impl:mapService")).getMapServiceContext().getMapContainer(((MapClearCodec.RequestParameters)this.parameters).name)).getMapConfig().isStatisticsEnabled()) {
            LocalMapStatsProvider localMapStatsProvider = mapServiceContext.getLocalMapStatsProvider();
            localMapStatsProvider.getLocalMapStatsImpl(((MapClearCodec.RequestParameters)this.parameters).name).incrementOtherOperations();
        }
        return null;
    }

    @Override
    protected MapClearCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapClearCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapClearCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapClearCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapClearCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "clear";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

