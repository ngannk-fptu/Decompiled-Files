/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomiclong;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicLongAddAndGetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomiclong.operations.AddAndGetOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicLongAddAndGetMessageTask
extends AbstractPartitionMessageTask<AtomicLongAddAndGetCodec.RequestParameters> {
    public AtomicLongAddAndGetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new AddAndGetOperation(((AtomicLongAddAndGetCodec.RequestParameters)this.parameters).name, ((AtomicLongAddAndGetCodec.RequestParameters)this.parameters).delta);
    }

    @Override
    protected AtomicLongAddAndGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicLongAddAndGetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicLongAddAndGetCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((AtomicLongAddAndGetCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicLongAddAndGetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "addAndGet";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((AtomicLongAddAndGetCodec.RequestParameters)this.parameters).delta};
    }
}

