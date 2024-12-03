/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomiclong;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicLongDecrementAndGetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomiclong.operations.AddAndGetOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicLongDecrementAndGetMessageTask
extends AbstractPartitionMessageTask<AtomicLongDecrementAndGetCodec.RequestParameters> {
    public AtomicLongDecrementAndGetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new AddAndGetOperation(((AtomicLongDecrementAndGetCodec.RequestParameters)this.parameters).name, -1L);
    }

    @Override
    protected AtomicLongDecrementAndGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicLongDecrementAndGetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicLongDecrementAndGetCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((AtomicLongDecrementAndGetCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicLongDecrementAndGetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "decrementAndGet";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

