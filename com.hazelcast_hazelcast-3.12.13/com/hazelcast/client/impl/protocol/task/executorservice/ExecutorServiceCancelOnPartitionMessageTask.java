/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.executorservice;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ExecutorServiceCancelOnPartitionCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.executor.impl.operations.CancellationOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class ExecutorServiceCancelOnPartitionMessageTask
extends AbstractPartitionMessageTask<ExecutorServiceCancelOnPartitionCodec.RequestParameters> {
    public ExecutorServiceCancelOnPartitionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new CancellationOperation(((ExecutorServiceCancelOnPartitionCodec.RequestParameters)this.parameters).uuid, ((ExecutorServiceCancelOnPartitionCodec.RequestParameters)this.parameters).interrupt);
    }

    @Override
    protected ExecutorServiceCancelOnPartitionCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ExecutorServiceCancelOnPartitionCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ExecutorServiceCancelOnPartitionCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:executorService";
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getMethodName() {
        return "cancel";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

