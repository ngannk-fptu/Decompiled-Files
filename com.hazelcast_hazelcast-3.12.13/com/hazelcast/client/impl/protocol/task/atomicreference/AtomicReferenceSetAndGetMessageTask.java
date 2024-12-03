/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomicreference;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceSetAndGetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomicreference.operations.SetAndGetOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.AtomicReferencePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicReferenceSetAndGetMessageTask
extends AbstractPartitionMessageTask<AtomicReferenceSetAndGetCodec.RequestParameters> {
    public AtomicReferenceSetAndGetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new SetAndGetOperation(((AtomicReferenceSetAndGetCodec.RequestParameters)this.parameters).name, ((AtomicReferenceSetAndGetCodec.RequestParameters)this.parameters).newValue);
    }

    @Override
    protected AtomicReferenceSetAndGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicReferenceSetAndGetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicReferenceSetAndGetCodec.encodeResponse((Data)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicReferenceService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicReferencePermission(((AtomicReferenceSetAndGetCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicReferenceSetAndGetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "setAndGet";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicReferenceSetAndGetCodec.RequestParameters)this.parameters).newValue};
    }
}

