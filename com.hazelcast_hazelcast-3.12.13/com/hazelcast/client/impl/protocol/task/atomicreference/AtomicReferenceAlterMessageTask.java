/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomicreference;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceAlterCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomicreference.operations.AlterOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicReferencePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicReferenceAlterMessageTask
extends AbstractPartitionMessageTask<AtomicReferenceAlterCodec.RequestParameters> {
    public AtomicReferenceAlterMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new AlterOperation(((AtomicReferenceAlterCodec.RequestParameters)this.parameters).name, ((AtomicReferenceAlterCodec.RequestParameters)this.parameters).function);
    }

    @Override
    protected AtomicReferenceAlterCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicReferenceAlterCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicReferenceAlterCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicReferenceService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicReferencePermission(((AtomicReferenceAlterCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicReferenceAlterCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "alter";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicReferenceAlterCodec.RequestParameters)this.parameters).function};
    }
}

