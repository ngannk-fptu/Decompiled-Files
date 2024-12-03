/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomiclong;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicLongGetAndAlterCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomiclong.operations.GetAndAlterOperation;
import com.hazelcast.core.IFunction;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicLongGetAndAlterMessageTask
extends AbstractPartitionMessageTask<AtomicLongGetAndAlterCodec.RequestParameters> {
    public AtomicLongGetAndAlterMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        IFunction function = (IFunction)this.serializationService.toObject(((AtomicLongGetAndAlterCodec.RequestParameters)this.parameters).function);
        return new GetAndAlterOperation(((AtomicLongGetAndAlterCodec.RequestParameters)this.parameters).name, function);
    }

    @Override
    protected AtomicLongGetAndAlterCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicLongGetAndAlterCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicLongGetAndAlterCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((AtomicLongGetAndAlterCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicLongGetAndAlterCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getAndAlter";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicLongGetAndAlterCodec.RequestParameters)this.parameters).function};
    }
}

