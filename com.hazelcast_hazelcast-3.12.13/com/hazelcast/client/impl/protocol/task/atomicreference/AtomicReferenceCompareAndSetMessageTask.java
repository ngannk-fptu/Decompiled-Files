/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomicreference;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceCompareAndSetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomicreference.operations.CompareAndSetOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicReferencePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicReferenceCompareAndSetMessageTask
extends AbstractPartitionMessageTask<AtomicReferenceCompareAndSetCodec.RequestParameters> {
    public AtomicReferenceCompareAndSetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CompareAndSetOperation(((AtomicReferenceCompareAndSetCodec.RequestParameters)this.parameters).name, ((AtomicReferenceCompareAndSetCodec.RequestParameters)this.parameters).expected, ((AtomicReferenceCompareAndSetCodec.RequestParameters)this.parameters).updated);
    }

    @Override
    protected AtomicReferenceCompareAndSetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicReferenceCompareAndSetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicReferenceCompareAndSetCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicReferenceService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicReferencePermission(((AtomicReferenceCompareAndSetCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicReferenceCompareAndSetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "compareAndSet";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicReferenceCompareAndSetCodec.RequestParameters)this.parameters).expected, ((AtomicReferenceCompareAndSetCodec.RequestParameters)this.parameters).updated};
    }
}

