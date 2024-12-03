/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.lock;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.LockIsLockedByCurrentThreadCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.InternalLockNamespace;
import com.hazelcast.concurrent.lock.operations.IsLockedOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.security.permission.LockPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class LockIsLockedByCurrentThreadMessageTask
extends AbstractPartitionMessageTask<LockIsLockedByCurrentThreadCodec.RequestParameters> {
    public LockIsLockedByCurrentThreadMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        Object key = this.serializationService.toData((Object)((LockIsLockedByCurrentThreadCodec.RequestParameters)this.parameters).name, StringPartitioningStrategy.INSTANCE);
        return new IsLockedOperation(new InternalLockNamespace(((LockIsLockedByCurrentThreadCodec.RequestParameters)this.parameters).name), (Data)key, ((LockIsLockedByCurrentThreadCodec.RequestParameters)this.parameters).threadId);
    }

    @Override
    protected LockIsLockedByCurrentThreadCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return LockIsLockedByCurrentThreadCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return LockIsLockedByCurrentThreadCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(((LockIsLockedByCurrentThreadCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((LockIsLockedByCurrentThreadCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "isLockedByCurrentThread";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

