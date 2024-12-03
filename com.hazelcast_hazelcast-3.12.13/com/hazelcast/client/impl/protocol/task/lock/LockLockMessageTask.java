/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.lock;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.LockLockCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.InternalLockNamespace;
import com.hazelcast.concurrent.lock.operations.LockOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.security.permission.LockPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class LockLockMessageTask
extends AbstractPartitionMessageTask<LockLockCodec.RequestParameters> {
    public LockLockMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        Object key = this.serializationService.toData((Object)((LockLockCodec.RequestParameters)this.parameters).name, StringPartitioningStrategy.INSTANCE);
        return new LockOperation(new InternalLockNamespace(((LockLockCodec.RequestParameters)this.parameters).name), (Data)key, ((LockLockCodec.RequestParameters)this.parameters).threadId, ((LockLockCodec.RequestParameters)this.parameters).leaseTime, -1L, ((LockLockCodec.RequestParameters)this.parameters).referenceId);
    }

    @Override
    protected LockLockCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return LockLockCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return LockLockCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(((LockLockCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((LockLockCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "lock";
    }

    @Override
    public Object[] getParameters() {
        if (((LockLockCodec.RequestParameters)this.parameters).leaseTime == -1L) {
            return null;
        }
        return new Object[]{((LockLockCodec.RequestParameters)this.parameters).leaseTime, TimeUnit.MILLISECONDS};
    }
}

