/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomicreference;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceAlterAndGetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomicreference.operations.AlterAndGetOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicReferencePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicReferenceAlterAndGetMessageTask
extends AbstractPartitionMessageTask<AtomicReferenceAlterAndGetCodec.RequestParameters> {
    public AtomicReferenceAlterAndGetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new AlterAndGetOperation(((AtomicReferenceAlterAndGetCodec.RequestParameters)this.parameters).name, ((AtomicReferenceAlterAndGetCodec.RequestParameters)this.parameters).function);
    }

    @Override
    protected AtomicReferenceAlterAndGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicReferenceAlterAndGetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicReferenceAlterAndGetCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicReferenceService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicReferencePermission(((AtomicReferenceAlterAndGetCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicReferenceAlterAndGetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "alterAndGet";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicReferenceAlterAndGetCodec.RequestParameters)this.parameters).function};
    }
}

