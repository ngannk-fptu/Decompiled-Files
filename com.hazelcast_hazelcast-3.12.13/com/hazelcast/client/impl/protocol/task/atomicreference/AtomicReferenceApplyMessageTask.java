/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomicreference;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceApplyCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomicreference.operations.ApplyOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.AtomicReferencePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicReferenceApplyMessageTask
extends AbstractPartitionMessageTask<AtomicReferenceApplyCodec.RequestParameters> {
    public AtomicReferenceApplyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ApplyOperation(((AtomicReferenceApplyCodec.RequestParameters)this.parameters).name, ((AtomicReferenceApplyCodec.RequestParameters)this.parameters).function);
    }

    @Override
    protected AtomicReferenceApplyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicReferenceApplyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicReferenceApplyCodec.encodeResponse((Data)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicReferenceService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicReferencePermission(((AtomicReferenceApplyCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicReferenceApplyCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "apply";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicReferenceApplyCodec.RequestParameters)this.parameters).function};
    }
}

