/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomicreference;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceClearCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomicreference.operations.SetOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicReferencePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicReferenceClearMessageTask
extends AbstractPartitionMessageTask<AtomicReferenceClearCodec.RequestParameters> {
    public AtomicReferenceClearMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new SetOperation(((AtomicReferenceClearCodec.RequestParameters)this.parameters).name, null);
    }

    @Override
    protected AtomicReferenceClearCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicReferenceClearCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicReferenceClearCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicReferenceService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicReferencePermission(((AtomicReferenceClearCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicReferenceClearCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "clear";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

