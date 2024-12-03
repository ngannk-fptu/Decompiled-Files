/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.condition;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ConditionBeforeAwaitCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.InternalLockNamespace;
import com.hazelcast.concurrent.lock.operations.BeforeAwaitOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.LockPermission;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ConditionBeforeAwaitMessageTask
extends AbstractPartitionMessageTask<ConditionBeforeAwaitCodec.RequestParameters> {
    public ConditionBeforeAwaitMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        Object key = this.serializationService.toData(((ConditionBeforeAwaitCodec.RequestParameters)this.parameters).lockName);
        InternalLockNamespace namespace = new InternalLockNamespace(((ConditionBeforeAwaitCodec.RequestParameters)this.parameters).lockName);
        return new BeforeAwaitOperation((ObjectNamespace)namespace, (Data)key, ((ConditionBeforeAwaitCodec.RequestParameters)this.parameters).threadId, ((ConditionBeforeAwaitCodec.RequestParameters)this.parameters).name, ((ConditionBeforeAwaitCodec.RequestParameters)this.parameters).referenceId);
    }

    @Override
    protected ConditionBeforeAwaitCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ConditionBeforeAwaitCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ConditionBeforeAwaitCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(((ConditionBeforeAwaitCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ConditionBeforeAwaitCodec.RequestParameters)this.parameters).lockName;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

