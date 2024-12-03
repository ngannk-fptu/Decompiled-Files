/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapIsLockedCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.operations.IsLockedOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class MultiMapIsLockedMessageTask
extends AbstractPartitionMessageTask<MultiMapIsLockedCodec.RequestParameters> {
    public MultiMapIsLockedMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        DistributedObjectNamespace namespace = new DistributedObjectNamespace("hz:impl:multiMapService", ((MultiMapIsLockedCodec.RequestParameters)this.parameters).name);
        return new IsLockedOperation(namespace, ((MultiMapIsLockedCodec.RequestParameters)this.parameters).key);
    }

    @Override
    protected MultiMapIsLockedCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapIsLockedCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapIsLockedCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public String getDistributedObjectType() {
        return "hz:impl:multiMapService";
    }

    @Override
    public String getMethodName() {
        return "isLocked";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((MultiMapIsLockedCodec.RequestParameters)this.parameters).key};
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapIsLockedCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapIsLockedCodec.RequestParameters)this.parameters).name;
    }
}

