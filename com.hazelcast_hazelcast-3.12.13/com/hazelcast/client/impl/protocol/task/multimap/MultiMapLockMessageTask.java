/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.multimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.MultiMapLockCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.operations.LockOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class MultiMapLockMessageTask
extends AbstractPartitionMessageTask<MultiMapLockCodec.RequestParameters> {
    public MultiMapLockMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        DistributedObjectNamespace namespace = this.getNamespace();
        return new LockOperation(namespace, ((MultiMapLockCodec.RequestParameters)this.parameters).key, ((MultiMapLockCodec.RequestParameters)this.parameters).threadId, ((MultiMapLockCodec.RequestParameters)this.parameters).ttl, -1L, ((MultiMapLockCodec.RequestParameters)this.parameters).referenceId);
    }

    private DistributedObjectNamespace getNamespace() {
        return new DistributedObjectNamespace("hz:impl:multiMapService", ((MultiMapLockCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return MultiMapLockCodec.encodeResponse();
    }

    @Override
    protected MultiMapLockCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return MultiMapLockCodec.decodeRequest(clientMessage);
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
        return "lock";
    }

    @Override
    public Object[] getParameters() {
        if (((MultiMapLockCodec.RequestParameters)this.parameters).ttl == -1L) {
            return new Object[]{((MultiMapLockCodec.RequestParameters)this.parameters).key};
        }
        return new Object[]{((MultiMapLockCodec.RequestParameters)this.parameters).key, ((MultiMapLockCodec.RequestParameters)this.parameters).ttl, TimeUnit.MILLISECONDS};
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(((MultiMapLockCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((MultiMapLockCodec.RequestParameters)this.parameters).name;
    }
}

