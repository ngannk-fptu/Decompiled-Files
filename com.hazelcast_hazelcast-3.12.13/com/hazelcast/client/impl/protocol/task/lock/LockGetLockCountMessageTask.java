/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.lock;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.LockGetLockCountCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.InternalLockNamespace;
import com.hazelcast.concurrent.lock.operations.GetLockCountOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.security.permission.LockPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class LockGetLockCountMessageTask
extends AbstractPartitionMessageTask<LockGetLockCountCodec.RequestParameters> {
    public LockGetLockCountMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        Object key = this.serializationService.toData((Object)((LockGetLockCountCodec.RequestParameters)this.parameters).name, StringPartitioningStrategy.INSTANCE);
        return new GetLockCountOperation(new InternalLockNamespace(((LockGetLockCountCodec.RequestParameters)this.parameters).name), (Data)key);
    }

    @Override
    protected LockGetLockCountCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return LockGetLockCountCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return LockGetLockCountCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(((LockGetLockCountCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((LockGetLockCountCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getLockCount";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

