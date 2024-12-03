/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.lock;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.LockGetRemainingLeaseTimeCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.InternalLockNamespace;
import com.hazelcast.concurrent.lock.operations.GetRemainingLeaseTimeOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.security.permission.LockPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class LockGetRemainingLeaseTimeMessageTask
extends AbstractPartitionMessageTask<LockGetRemainingLeaseTimeCodec.RequestParameters> {
    public LockGetRemainingLeaseTimeMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        Object key = this.serializationService.toData((Object)((LockGetRemainingLeaseTimeCodec.RequestParameters)this.parameters).name, StringPartitioningStrategy.INSTANCE);
        return new GetRemainingLeaseTimeOperation(new InternalLockNamespace(((LockGetRemainingLeaseTimeCodec.RequestParameters)this.parameters).name), (Data)key);
    }

    @Override
    protected LockGetRemainingLeaseTimeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return LockGetRemainingLeaseTimeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return LockGetRemainingLeaseTimeCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(((LockGetRemainingLeaseTimeCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((LockGetRemainingLeaseTimeCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getRemainingLeaseTime";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

