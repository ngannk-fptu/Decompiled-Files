/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapIsEmptyCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAllPartitionsMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.LocalMapStatsUtil;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.operation.IsEmptyOperationFactory;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.Map;

public class MapIsEmptyMessageTask
extends AbstractAllPartitionsMessageTask<MapIsEmptyCodec.RequestParameters> {
    public MapIsEmptyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        return new IsEmptyOperationFactory(((MapIsEmptyCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        MapService mapService = (MapService)this.getService("hz:impl:mapService");
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        boolean response = true;
        for (Object result : map.values()) {
            boolean isEmpty = (Boolean)mapServiceContext.toObject(result);
            if (isEmpty) continue;
            response = false;
        }
        LocalMapStatsUtil.incrementOtherOperationsCount(mapService, ((MapIsEmptyCodec.RequestParameters)this.parameters).name);
        return response;
    }

    @Override
    protected MapIsEmptyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapIsEmptyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapIsEmptyCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapIsEmptyCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapIsEmptyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "isEmpty";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

