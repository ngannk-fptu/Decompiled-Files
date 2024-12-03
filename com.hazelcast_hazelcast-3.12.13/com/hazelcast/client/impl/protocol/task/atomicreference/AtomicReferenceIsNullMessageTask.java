/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomicreference;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceIsNullCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomicreference.operations.IsNullOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicReferencePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicReferenceIsNullMessageTask
extends AbstractPartitionMessageTask<AtomicReferenceIsNullCodec.RequestParameters> {
    public AtomicReferenceIsNullMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new IsNullOperation(((AtomicReferenceIsNullCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected AtomicReferenceIsNullCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicReferenceIsNullCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicReferenceIsNullCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicReferenceService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicReferencePermission(((AtomicReferenceIsNullCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicReferenceIsNullCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "isNull";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

