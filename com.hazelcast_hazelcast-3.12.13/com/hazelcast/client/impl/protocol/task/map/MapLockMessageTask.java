/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapLockCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.operations.LockOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class MapLockMessageTask
extends AbstractPartitionMessageTask<MapLockCodec.RequestParameters> {
    public MapLockMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new LockOperation(this.getNamespace(), ((MapLockCodec.RequestParameters)this.parameters).key, ((MapLockCodec.RequestParameters)this.parameters).threadId, ((MapLockCodec.RequestParameters)this.parameters).ttl, -1L, ((MapLockCodec.RequestParameters)this.parameters).referenceId);
    }

    @Override
    protected MapLockCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapLockCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapLockCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public String getDistributedObjectType() {
        return "hz:impl:mapService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapLockCodec.RequestParameters)this.parameters).name, "lock");
    }

    private ObjectNamespace getNamespace() {
        return MapService.getObjectNamespace(((MapLockCodec.RequestParameters)this.parameters).name);
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapLockCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "lock";
    }

    @Override
    public Object[] getParameters() {
        if (((MapLockCodec.RequestParameters)this.parameters).ttl == -1L) {
            return new Object[]{((MapLockCodec.RequestParameters)this.parameters).key};
        }
        return new Object[]{((MapLockCodec.RequestParameters)this.parameters).key, ((MapLockCodec.RequestParameters)this.parameters).ttl, TimeUnit.MILLISECONDS};
    }
}

