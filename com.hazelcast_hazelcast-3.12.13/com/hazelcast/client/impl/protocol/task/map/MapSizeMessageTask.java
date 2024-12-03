/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapSizeCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapAllPartitionsMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.LocalMapStatsUtil;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.util.MapUtil;
import java.security.Permission;
import java.util.Map;

public class MapSizeMessageTask
extends AbstractMapAllPartitionsMessageTask<MapSizeCodec.RequestParameters> {
    public MapSizeMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        String mapName = ((MapSizeCodec.RequestParameters)this.parameters).name;
        return this.getOperationProvider(mapName).createMapSizeOperationFactory(mapName);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        long total = 0L;
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        for (Object result : map.values()) {
            Integer size = (Integer)mapService.getMapServiceContext().toObject(result);
            total += (long)size.intValue();
        }
        LocalMapStatsUtil.incrementOtherOperationsCount(mapService, ((MapSizeCodec.RequestParameters)this.parameters).name);
        return MapUtil.toIntSize(total);
    }

    @Override
    protected MapSizeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapSizeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapSizeCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapSizeCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapSizeCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "size";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

