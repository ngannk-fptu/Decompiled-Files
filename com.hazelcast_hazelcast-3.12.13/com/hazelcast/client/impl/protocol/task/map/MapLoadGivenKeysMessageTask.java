/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapLoadGivenKeysCodec;
import com.hazelcast.client.impl.protocol.task.map.AbstractMapAllPartitionsMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.OperationFactory;
import java.security.Permission;
import java.util.Arrays;
import java.util.Map;

public class MapLoadGivenKeysMessageTask
extends AbstractMapAllPartitionsMessageTask<MapLoadGivenKeysCodec.RequestParameters> {
    public MapLoadGivenKeysMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected OperationFactory createOperationFactory() {
        Data[] keys = ((MapLoadGivenKeysCodec.RequestParameters)this.parameters).keys.toArray(new Data[0]);
        MapOperationProvider operationProvider = this.getOperationProvider(((MapLoadGivenKeysCodec.RequestParameters)this.parameters).name);
        return operationProvider.createLoadAllOperationFactory(((MapLoadGivenKeysCodec.RequestParameters)this.parameters).name, Arrays.asList(keys), ((MapLoadGivenKeysCodec.RequestParameters)this.parameters).replaceExistingValues);
    }

    @Override
    protected Object reduce(Map<Integer, Object> map) {
        return MapLoadGivenKeysCodec.encodeResponse();
    }

    @Override
    protected MapLoadGivenKeysCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapLoadGivenKeysCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapLoadGivenKeysCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapLoadGivenKeysCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapLoadGivenKeysCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "loadAll";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapLoadGivenKeysCodec.RequestParameters)this.parameters).keys, ((MapLoadGivenKeysCodec.RequestParameters)this.parameters).replaceExistingValues};
    }
}

