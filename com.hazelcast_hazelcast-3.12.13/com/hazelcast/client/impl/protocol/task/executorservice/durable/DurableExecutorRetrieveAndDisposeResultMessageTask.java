/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.executorservice.durable;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DurableExecutorRetrieveAndDisposeResultCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.durableexecutor.impl.operations.RetrieveAndDisposeResultOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class DurableExecutorRetrieveAndDisposeResultMessageTask
extends AbstractPartitionMessageTask<DurableExecutorRetrieveAndDisposeResultCodec.RequestParameters> {
    public DurableExecutorRetrieveAndDisposeResultMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new RetrieveAndDisposeResultOperation(((DurableExecutorRetrieveAndDisposeResultCodec.RequestParameters)this.parameters).name, ((DurableExecutorRetrieveAndDisposeResultCodec.RequestParameters)this.parameters).sequence);
    }

    @Override
    protected DurableExecutorRetrieveAndDisposeResultCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DurableExecutorRetrieveAndDisposeResultCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        Object data = this.serializationService.toData(response);
        return DurableExecutorRetrieveAndDisposeResultCodec.encodeResponse(data);
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
        return ((DurableExecutorRetrieveAndDisposeResultCodec.RequestParameters)this.parameters).name;
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

