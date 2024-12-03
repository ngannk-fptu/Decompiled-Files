/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomiclong;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicLongCompareAndSetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomiclong.operations.CompareAndSetOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicLongCompareAndSetMessageTask
extends AbstractPartitionMessageTask<AtomicLongCompareAndSetCodec.RequestParameters> {
    public AtomicLongCompareAndSetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CompareAndSetOperation(((AtomicLongCompareAndSetCodec.RequestParameters)this.parameters).name, ((AtomicLongCompareAndSetCodec.RequestParameters)this.parameters).expected, ((AtomicLongCompareAndSetCodec.RequestParameters)this.parameters).updated);
    }

    @Override
    protected AtomicLongCompareAndSetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicLongCompareAndSetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicLongCompareAndSetCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((AtomicLongCompareAndSetCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicLongCompareAndSetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "compareAndSet";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicLongCompareAndSetCodec.RequestParameters)this.parameters).expected, ((AtomicLongCompareAndSetCodec.RequestParameters)this.parameters).updated};
    }
}

