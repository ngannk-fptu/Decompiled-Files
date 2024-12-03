/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapUnlockCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.operations.UnlockOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MapUnlockMessageTask
extends AbstractPartitionMessageTask<MapUnlockCodec.RequestParameters> {
    public MapUnlockMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new UnlockOperation(this.getNamespace(), ((MapUnlockCodec.RequestParameters)this.parameters).key, ((MapUnlockCodec.RequestParameters)this.parameters).threadId, false, ((MapUnlockCodec.RequestParameters)this.parameters).referenceId);
    }

    @Override
    protected MapUnlockCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapUnlockCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapUnlockCodec.encodeResponse();
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
        return new MapPermission(((MapUnlockCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapUnlockCodec.RequestParameters)this.parameters).name;
    }

    private ObjectNamespace getNamespace() {
        return MapService.getObjectNamespace(((MapUnlockCodec.RequestParameters)this.parameters).name);
    }

    @Override
    public String getMethodName() {
        return "unlock";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MapUnlockCodec.RequestParameters)this.parameters).key};
    }
}

