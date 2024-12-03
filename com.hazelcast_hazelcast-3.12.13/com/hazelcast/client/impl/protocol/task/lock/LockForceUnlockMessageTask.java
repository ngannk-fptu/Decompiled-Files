/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.lock;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.LockForceUnlockCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.InternalLockNamespace;
import com.hazelcast.concurrent.lock.operations.UnlockOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.security.permission.LockPermission;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class LockForceUnlockMessageTask
extends AbstractPartitionMessageTask<LockForceUnlockCodec.RequestParameters> {
    public LockForceUnlockMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        Object key = this.serializationService.toData((Object)((LockForceUnlockCodec.RequestParameters)this.parameters).name, StringPartitioningStrategy.INSTANCE);
        return new UnlockOperation((ObjectNamespace)new InternalLockNamespace(((LockForceUnlockCodec.RequestParameters)this.parameters).name), (Data)key, -1L, true, ((LockForceUnlockCodec.RequestParameters)this.parameters).referenceId);
    }

    @Override
    protected LockForceUnlockCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return LockForceUnlockCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return LockForceUnlockCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(((LockForceUnlockCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((LockForceUnlockCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "forceUnlock";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

