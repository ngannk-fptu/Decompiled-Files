/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.condition;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ConditionSignalAllCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.lock.InternalLockNamespace;
import com.hazelcast.concurrent.lock.operations.SignalOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.LockPermission;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ConditionSignalAllMessageTask
extends AbstractPartitionMessageTask<ConditionSignalAllCodec.RequestParameters> {
    public ConditionSignalAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        Object key = this.serializationService.toData(((ConditionSignalAllCodec.RequestParameters)this.parameters).lockName);
        InternalLockNamespace namespace = new InternalLockNamespace(((ConditionSignalAllCodec.RequestParameters)this.parameters).lockName);
        return new SignalOperation((ObjectNamespace)namespace, (Data)key, ((ConditionSignalAllCodec.RequestParameters)this.parameters).threadId, ((ConditionSignalAllCodec.RequestParameters)this.parameters).name, true);
    }

    @Override
    protected ConditionSignalAllCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ConditionSignalAllCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ConditionSignalAllCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(((ConditionSignalAllCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ConditionSignalAllCodec.RequestParameters)this.parameters).lockName;
    }

    @Override
    public String getMethodName() {
        return "signalAll";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

