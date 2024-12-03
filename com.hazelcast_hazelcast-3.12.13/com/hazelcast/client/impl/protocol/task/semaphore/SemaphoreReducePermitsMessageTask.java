/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.semaphore;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SemaphoreReducePermitsCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.semaphore.operations.ReduceOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SemaphorePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class SemaphoreReducePermitsMessageTask
extends AbstractPartitionMessageTask<SemaphoreReducePermitsCodec.RequestParameters> {
    public SemaphoreReducePermitsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ReduceOperation(((SemaphoreReducePermitsCodec.RequestParameters)this.parameters).name, ((SemaphoreReducePermitsCodec.RequestParameters)this.parameters).reduction);
    }

    @Override
    protected SemaphoreReducePermitsCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SemaphoreReducePermitsCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SemaphoreReducePermitsCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:semaphoreService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SemaphorePermission(((SemaphoreReducePermitsCodec.RequestParameters)this.parameters).name, "acquire");
    }

    @Override
    public String getDistributedObjectName() {
        return ((SemaphoreReducePermitsCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "reducePermits";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((SemaphoreReducePermitsCodec.RequestParameters)this.parameters).reduction};
    }
}

