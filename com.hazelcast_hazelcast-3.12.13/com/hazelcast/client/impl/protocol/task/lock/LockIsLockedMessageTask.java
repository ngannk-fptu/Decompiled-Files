/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.lock;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.LockIsLockedCodec;
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

public class LockIsLockedMessageTask
extends AbstractPartitionMessageTask<LockIsLockedCodec.RequestParameters> {
    public LockIsLockedMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        Object key = this.serializationService.toData((Object)((LockIsLockedCodec.RequestParameters)this.parameters).name, StringPartitioningStrategy.INSTANCE);
        return new IsLockedOperation(new InternalLockNamespace(((LockIsLockedCodec.RequestParameters)this.parameters).name), (Data)key);
    }

    @Override
    protected LockIsLockedCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return LockIsLockedCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return LockIsLockedCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(((LockIsLockedCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((LockIsLockedCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "isLocked";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

