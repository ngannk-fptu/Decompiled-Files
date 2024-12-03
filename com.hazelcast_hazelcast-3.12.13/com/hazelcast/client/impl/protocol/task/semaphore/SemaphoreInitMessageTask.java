/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.semaphore;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SemaphoreInitCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.semaphore.operations.InitOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SemaphorePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class SemaphoreInitMessageTask
extends AbstractPartitionMessageTask<SemaphoreInitCodec.RequestParameters> {
    public SemaphoreInitMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new InitOperation(((SemaphoreInitCodec.RequestParameters)this.parameters).name, ((SemaphoreInitCodec.RequestParameters)this.parameters).permits);
    }

    @Override
    protected SemaphoreInitCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SemaphoreInitCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SemaphoreInitCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:semaphoreService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SemaphorePermission(((SemaphoreInitCodec.RequestParameters)this.parameters).name, "release");
    }

    @Override
    public String getDistributedObjectName() {
        return ((SemaphoreInitCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "init";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((SemaphoreInitCodec.RequestParameters)this.parameters).permits};
    }
}

