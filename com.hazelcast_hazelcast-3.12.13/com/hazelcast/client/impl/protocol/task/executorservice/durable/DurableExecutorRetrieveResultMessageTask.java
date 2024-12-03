/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.executorservice.durable;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DurableExecutorRetrieveResultCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.durableexecutor.impl.operations.RetrieveResultOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class DurableExecutorRetrieveResultMessageTask
extends AbstractPartitionMessageTask<DurableExecutorRetrieveResultCodec.RequestParameters> {
    public DurableExecutorRetrieveResultMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new RetrieveResultOperation(((DurableExecutorRetrieveResultCodec.RequestParameters)this.parameters).name, ((DurableExecutorRetrieveResultCodec.RequestParameters)this.parameters).sequence);
    }

    @Override
    protected DurableExecutorRetrieveResultCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DurableExecutorRetrieveResultCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        Object data = this.serializationService.toData(response);
        return DurableExecutorRetrieveResultCodec.encodeResponse(data);
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
        return ((DurableExecutorRetrieveResultCodec.RequestParameters)this.parameters).name;
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

