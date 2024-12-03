/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomicreference;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceGetAndAlterCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomicreference.operations.GetAndAlterOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicReferencePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicReferenceGetAndAlterMessageTask
extends AbstractPartitionMessageTask<AtomicReferenceGetAndAlterCodec.RequestParameters> {
    public AtomicReferenceGetAndAlterMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new GetAndAlterOperation(((AtomicReferenceGetAndAlterCodec.RequestParameters)this.parameters).name, ((AtomicReferenceGetAndAlterCodec.RequestParameters)this.parameters).function);
    }

    @Override
    protected AtomicReferenceGetAndAlterCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicReferenceGetAndAlterCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicReferenceGetAndAlterCodec.encodeResponse(this.serializationService.toData(response));
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicReferenceService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicReferencePermission(((AtomicReferenceGetAndAlterCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicReferenceGetAndAlterCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getAndAlter";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicReferenceGetAndAlterCodec.RequestParameters)this.parameters).function};
    }
}

