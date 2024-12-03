/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapContainsValueCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapAllPartitionsMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.LocalMapStatsUtil;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.Map;

public class MapContainsValueMessageTask
extends AbstractMapAllPartitionsMessageTask<MapContainsValueCodec.RequestParameters> {
    public MapContainsValueMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        MapOperationProvider operationProvider = this.getOperationProvider(((MapContainsValueCodec.RequestParameters)this.parameters).name);
        return operationProvider.createContainsValueOperationFactory(((MapContainsValueCodec.RequestParameters)this.parameters).name, ((MapContainsValueCodec.RequestParameters)this.parameters).value);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        boolean result = false;
        for (Object contains : map.values()) {
            if (!Boolean.TRUE.equals(contains)) continue;
            result = true;
            break;
        }
        LocalMapStatsUtil.incrementOtherOperationsCount((MapService)this.getService("hz:impl:mapService"), ((MapContainsValueCodec.RequestParameters)this.parameters).name);
        return result;
    }

    @Override
    protected MapContainsValueCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapContainsValueCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapContainsValueCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapContainsValueCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapContainsValueCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "containsValue";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapContainsValueCodec.RequestParameters)this.parameters).value};
    }
}

