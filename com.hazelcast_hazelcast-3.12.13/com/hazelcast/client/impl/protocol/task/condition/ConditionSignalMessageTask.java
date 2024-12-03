/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.condition;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ConditionSignalCodec;
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

public class ConditionSignalMessageTask
extends AbstractPartitionMessageTask<ConditionSignalCodec.RequestParameters> {
    public ConditionSignalMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        Object key = this.serializationService.toData(((ConditionSignalCodec.RequestParameters)this.parameters).lockName);
        InternalLockNamespace namespace = new InternalLockNamespace(((ConditionSignalCodec.RequestParameters)this.parameters).lockName);
        return new SignalOperation((ObjectNamespace)namespace, (Data)key, ((ConditionSignalCodec.RequestParameters)this.parameters).threadId, ((ConditionSignalCodec.RequestParameters)this.parameters).name, false);
    }

    @Override
    protected ConditionSignalCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ConditionSignalCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ConditionSignalCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(((ConditionSignalCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((ConditionSignalCodec.RequestParameters)this.parameters).lockName;
    }

    @Override
    public String getMethodName() {
        return "signal";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

