/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.atomiclong;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AtomicLongIncrementAndGetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.atomiclong.operations.AddAndGetOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.AtomicLongPermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class AtomicLongIncrementAndGetMessageTask
extends AbstractPartitionMessageTask<AtomicLongIncrementAndGetCodec.RequestParameters> {
    public AtomicLongIncrementAndGetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new AddAndGetOperation(((AtomicLongIncrementAndGetCodec.RequestParameters)this.parameters).name, 1L);
    }

    @Override
    protected AtomicLongIncrementAndGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return AtomicLongIncrementAndGetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return AtomicLongIncrementAndGetCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicLongService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new AtomicLongPermission(((AtomicLongIncrementAndGetCodec.RequestParameters)this.parameters).name, "modify");
    }

    @Override
    public String getDistributedObjectName() {
        return ((AtomicLongIncrementAndGetCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "incrementAndGet";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

