/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.executorservice.durable;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DurableExecutorDisposeResultCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.durableexecutor.impl.operations.DisposeResultOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class DurableExecutorDisposeResultMessageTask
extends AbstractPartitionMessageTask<DurableExecutorDisposeResultCodec.RequestParameters> {
    public DurableExecutorDisposeResultMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new DisposeResultOperation(((DurableExecutorDisposeResultCodec.RequestParameters)this.parameters).name, ((DurableExecutorDisposeResultCodec.RequestParameters)this.parameters).sequence);
    }

    @Override
    protected DurableExecutorDisposeResultCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DurableExecutorDisposeResultCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DurableExecutorDisposeResultCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:durableExecutorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return ((DurableExecutorDisposeResultCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

