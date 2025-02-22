/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapEvictAllCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapAllPartitionsMessageTask;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.instance.Node;
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

public class MapEvictAllMessageTask
extends AbstractMapAllPartitionsMessageTask<MapEvictAllCodec.RequestParameters> {
    public MapEvictAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        MapOperationProvider operationProvider = this.getOperationProvider(((MapEvictAllCodec.RequestParameters)this.parameters).name);
        return operationProvider.createEvictAllOperationFactory(((MapEvictAllCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        int evictedTotal = 0;
        for (Object result : map.values()) {
            Integer size = (Integer)mapServiceContext.toObject(result);
            evictedTotal += size.intValue();
        }
        if (evictedTotal > 0) {
            Address thisAddress = mapServiceContext.getNodeEngine().getThisAddress();
            MapEventPublisher mapEventPublisher = mapServiceContext.getMapEventPublisher();
            mapEventPublisher.publishMapEvent(thisAddress, ((MapEvictAllCodec.RequestParameters)this.parameters).name, EntryEventType.EVICT_ALL, evictedTotal);
        }
        return null;
    }

    @Override
    protected MapEvictAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapEvictAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapEvictAllCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapEvictAllCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapEvictAllCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "evictAll";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

