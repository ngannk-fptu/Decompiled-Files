/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomicreference;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceGetAndSetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomicreference.operations.GetAndSetOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.AtomicReferencePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicReferenceGetAndSetMessageTask
extends AbstractPartitionMessageTask<AtomicReferenceGetAndSetCodec.RequestParameters> {
    public AtomicReferenceGetAndSetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new GetAndSetOperation(((AtomicReferenceGetAndSetCodec.RequestParameters)this.parameters).name, ((AtomicReferenceGetAndSetCodec.RequestParameters)this.parameters).newValue);
    }

    @Override
    protected AtomicReferenceGetAndSetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicReferenceGetAndSetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicReferenceGetAndSetCodec.encodeResponse((Data)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicReferenceService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicReferencePermission(((AtomicReferenceGetAndSetCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicReferenceGetAndSetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getAndSet";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicReferenceGetAndSetCodec.RequestParameters)this.parameters).newValue};
    }
}

