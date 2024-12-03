/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapForceUnlockCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.operations.UnlockOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MapForceUnlockMessageTask
extends AbstractPartitionMessageTask<MapForceUnlockCodec.RequestParameters> {
    public MapForceUnlockMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new UnlockOperation(this.getNamespace(), ((MapForceUnlockCodec.RequestParameters)this.parameters).key, -1L, true, ((MapForceUnlockCodec.RequestParameters)this.parameters).referenceId);
    }

    @Override
    protected MapForceUnlockCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapForceUnlockCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapForceUnlockCodec.encodeResponse();
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
        return new MapPermission(((MapForceUnlockCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapForceUnlockCodec.RequestParameters)this.parameters).name;
    }

    private ObjectNamespace getNamespace() {
        return MapService.getObjectNamespace(((MapForceUnlockCodec.RequestParameters)this.parameters).name);
    }

    @Override
    public String getMethodName() {
        return "forceUnlock";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapForceUnlockCodec.RequestParameters)this.parameters).key};
    }
}

