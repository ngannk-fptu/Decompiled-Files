/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.lock;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.LockTryLockCodec;
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

public class LockTryLockMessageTask
extends AbstractPartitionMessageTask<LockTryLockCodec.RequestParameters> {
    public LockTryLockMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        Object key = this.serializationService.toData((Object)((LockTryLockCodec.RequestParameters)this.parameters).name, StringPartitioningStrategy.INSTANCE);
        return new LockOperation(new InternalLockNamespace(((LockTryLockCodec.RequestParameters)this.parameters).name), (Data)key, ((LockTryLockCodec.RequestParameters)this.parameters).threadId, ((LockTryLockCodec.RequestParameters)this.parameters).lease, ((LockTryLockCodec.RequestParameters)this.parameters).timeout, ((LockTryLockCodec.RequestParameters)this.parameters).referenceId);
    }

    @Override
    protected LockTryLockCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return LockTryLockCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return LockTryLockCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(((LockTryLockCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((LockTryLockCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "tryLock";
    }

    @Override
    public Object[] getParameters() {
        if (((LockTryLockCodec.RequestParameters)this.parameters).timeout == -1L) {
            return null;
        }
        return new Object[]{((LockTryLockCodec.RequestParameters)this.parameters).timeout, TimeUnit.MILLISECONDS};
    }
}

