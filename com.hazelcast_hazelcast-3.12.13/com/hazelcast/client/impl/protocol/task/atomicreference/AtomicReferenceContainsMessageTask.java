/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomicreference;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceContainsCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomicreference.operations.ContainsOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicReferencePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicReferenceContainsMessageTask
extends AbstractPartitionMessageTask<AtomicReferenceContainsCodec.RequestParameters> {
    public AtomicReferenceContainsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ContainsOperation(((AtomicReferenceContainsCodec.RequestParameters)this.parameters).name, ((AtomicReferenceContainsCodec.RequestParameters)this.parameters).expected);
    }

    @Override
    protected AtomicReferenceContainsCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicReferenceContainsCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicReferenceContainsCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicReferenceService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicReferencePermission(((AtomicReferenceContainsCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicReferenceContainsCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "contains";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicReferenceContainsCodec.RequestParameters)this.parameters).expected};
    }
}

