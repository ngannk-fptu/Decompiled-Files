/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapClearNearCacheCodec;
import com.hazelcast.client.impl.protocol.task.AbstractInvocationMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.operation.ClearNearCacheOperation;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import java.security.Permission;

@Deprecated
public class MapClearNearCacheMessageTask
extends AbstractInvocationMessageTask<MapClearNearCacheCodec.RequestParameters> {
    public MapClearNearCacheMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected InvocationBuilder getInvocationBuilder(Operation op) {
        InternalOperationService operationService = this.nodeEngine.getOperationService();
        return operationService.createInvocationBuilder(this.getServiceName(), op, ((MapClearNearCacheCodec.RequestParameters)this.parameters).target);
    }

    @Override
    protected Operation prepareOperation() {
        return new ClearNearCacheOperation(((MapClearNearCacheCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected MapClearNearCacheCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        this.parameters = MapClearNearCacheCodec.decodeRequest(clientMessage);
        ((MapClearNearCacheCodec.RequestParameters)this.parameters).target = this.clientEngine.memberAddressOf(((MapClearNearCacheCodec.RequestParameters)this.parameters).target);
        return (MapClearNearCacheCodec.RequestParameters)this.parameters;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapClearNearCacheCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapClearNearCacheCodec.RequestParameters)this.parameters).name, "remove");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapClearNearCacheCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

