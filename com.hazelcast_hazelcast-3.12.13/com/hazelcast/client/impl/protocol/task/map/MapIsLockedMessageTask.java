/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.map;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MapIsLockedCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.operations.IsLockedOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MapIsLockedMessageTask
extends AbstractPartitionMessageTask<MapIsLockedCodec.RequestParameters> {
    public MapIsLockedMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new IsLockedOperation(this.getNamespace(), ((MapIsLockedCodec.RequestParameters)this.parameters).key);
    }

    @Override
    protected MapIsLockedCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MapIsLockedCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MapIsLockedCodec.encodeResponse((Boolean)response);
    }

    @Override
    public Permission getRequiredPermission() {
        return new MapPermission(((MapIsLockedCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MapIsLockedCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "isLocked";
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
    public Object[] getParameters() {
        return new Object[]{((MapIsLockedCodec.RequestParameters)this.parameters).key};
    }

    private ObjectNamespace getNamespace() {
        return MapService.getObjectNamespace(((MapIsLockedCodec.RequestParameters)this.parameters).name);
    }
}

