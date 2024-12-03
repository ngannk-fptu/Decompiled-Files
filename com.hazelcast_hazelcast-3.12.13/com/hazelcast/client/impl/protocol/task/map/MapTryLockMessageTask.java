/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapTryLockCodec;
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

public class MapTryLockMessageTask
extends AbstractPartitionMessageTask<MapTryLockCodec.RequestParameters> {
    public MapTryLockMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new LockOperation(this.getNamespace(), ((MapTryLockCodec.RequestParameters)this.parameters).key, ((MapTryLockCodec.RequestParameters)this.parameters).threadId, ((MapTryLockCodec.RequestParameters)this.parameters).lease, ((MapTryLockCodec.RequestParameters)this.parameters).timeout, ((MapTryLockCodec.RequestParameters)this.parameters).referenceId);
    }

    @Override
    protected MapTryLockCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapTryLockCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapTryLockCodec.encodeResponse((Boolean)response);
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
        return new MapPermission(((MapTryLockCodec.RequestParameters)this.parameters).name, "lock");
    }

    private ObjectNamespace getNamespace() {
        return MapService.getObjectNamespace(((MapTryLockCodec.RequestParameters)this.parameters).name);
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapTryLockCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "tryLock";
    }

    @Override
    public Object[] getParameters() {
        if (((MapTryLockCodec.RequestParameters)this.parameters).timeout == 0L) {
            return new Object[]{((MapTryLockCodec.RequestParameters)this.parameters).key};
        }
        return new Object[]{((MapTryLockCodec.RequestParameters)this.parameters).key, ((MapTryLockCodec.RequestParameters)this.parameters).timeout, TimeUnit.MILLISECONDS};
    }
}

