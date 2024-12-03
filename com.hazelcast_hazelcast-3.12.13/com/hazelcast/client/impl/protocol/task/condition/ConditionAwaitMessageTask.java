/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.condition;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ConditionAwaitCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.InternalLockNamespace;
import com.hazelcast.concurrent.lock.operations.AwaitOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.LockPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class ConditionAwaitMessageTask
extends AbstractPartitionMessageTask<ConditionAwaitCodec.RequestParameters> {
    public ConditionAwaitMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        Object key = this.serializationService.toData(((ConditionAwaitCodec.RequestParameters)this.parameters).lockName);
        InternalLockNamespace namespace = new InternalLockNamespace(((ConditionAwaitCodec.RequestParameters)this.parameters).lockName);
        return new AwaitOperation(namespace, (Data)key, ((ConditionAwaitCodec.RequestParameters)this.parameters).threadId, ((ConditionAwaitCodec.RequestParameters)this.parameters).timeout, ((ConditionAwaitCodec.RequestParameters)this.parameters).name, ((ConditionAwaitCodec.RequestParameters)this.parameters).referenceId);
    }

    @Override
    protected ConditionAwaitCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ConditionAwaitCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ConditionAwaitCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(((ConditionAwaitCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ConditionAwaitCodec.RequestParameters)this.parameters).lockName;
    }

    @Override
    public String getMethodName() {
        return "await";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((ConditionAwaitCodec.RequestParameters)this.parameters).timeout, TimeUnit.MILLISECONDS};
    }
}

