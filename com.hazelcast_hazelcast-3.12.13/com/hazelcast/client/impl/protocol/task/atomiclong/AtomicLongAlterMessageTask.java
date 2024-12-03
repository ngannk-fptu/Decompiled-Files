/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomiclong;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicLongAlterCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomiclong.operations.AlterOperation;
import com.hazelcast.core.IFunction;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicLongAlterMessageTask
extends AbstractPartitionMessageTask<AtomicLongAlterCodec.RequestParameters> {
    public AtomicLongAlterMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        IFunction function = (IFunction)this.serializationService.toObject(((AtomicLongAlterCodec.RequestParameters)this.parameters).function);
        return new AlterOperation(((AtomicLongAlterCodec.RequestParameters)this.parameters).name, function);
    }

    @Override
    protected AtomicLongAlterCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicLongAlterCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicLongAlterCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((AtomicLongAlterCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicLongAlterCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "alter";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicLongAlterCodec.RequestParameters)this.parameters).function};
    }
}

