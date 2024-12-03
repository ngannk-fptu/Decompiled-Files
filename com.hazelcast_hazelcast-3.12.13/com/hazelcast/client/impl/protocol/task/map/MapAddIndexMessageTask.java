/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapAddIndexCodec;
import com.hazelcast.client.impl.protocol.task.AbstractAllPartitionsMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.AddIndexOperationFactory;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.Map;

public class MapAddIndexMessageTask
extends AbstractAllPartitionsMessageTask<MapAddIndexCodec.RequestParameters> {
    public MapAddIndexMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        return new AddIndexOperationFactory(((MapAddIndexCodec.RequestParameters)this.parameters).name, ((MapAddIndexCodec.RequestParameters)this.parameters).attribute, ((MapAddIndexCodec.RequestParameters)this.parameters).ordered);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        return null;
    }

    @Override
    protected MapAddIndexCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapAddIndexCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapAddIndexCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapAddIndexCodec.RequestParameters)this.parameters).name, "index");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapAddIndexCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "addIndex";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapAddIndexCodec.RequestParameters)this.parameters).attribute, ((MapAddIndexCodec.RequestParameters)this.parameters).ordered};
    }
}

