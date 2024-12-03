/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.semaphore;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SemaphoreAcquireCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.semaphore.operations.AcquireOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SemaphorePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class SemaphoreAcquireMessageTask
extends AbstractPartitionMessageTask<SemaphoreAcquireCodec.RequestParameters> {
    public SemaphoreAcquireMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new AcquireOperation(((SemaphoreAcquireCodec.RequestParameters)this.parameters).name, ((SemaphoreAcquireCodec.RequestParameters)this.parameters).permits, -1L);
    }

    @Override
    protected SemaphoreAcquireCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SemaphoreAcquireCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SemaphoreAcquireCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:semaphoreService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SemaphorePermission(((SemaphoreAcquireCodec.RequestParameters)this.parameters).name, "acquire");
    }

    @Override
    public String getDistributedObjectName() {
        return ((SemaphoreAcquireCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "acquire";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((SemaphoreAcquireCodec.RequestParameters)this.parameters).permits};
    }
}

