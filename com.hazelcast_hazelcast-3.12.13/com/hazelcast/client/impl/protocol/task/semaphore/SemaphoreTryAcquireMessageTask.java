/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.semaphore;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SemaphoreTryAcquireCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.semaphore.operations.AcquireOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SemaphorePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class SemaphoreTryAcquireMessageTask
extends AbstractPartitionMessageTask<SemaphoreTryAcquireCodec.RequestParameters> {
    public SemaphoreTryAcquireMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new AcquireOperation(((SemaphoreTryAcquireCodec.RequestParameters)this.parameters).name, ((SemaphoreTryAcquireCodec.RequestParameters)this.parameters).permits, ((SemaphoreTryAcquireCodec.RequestParameters)this.parameters).timeout);
    }

    @Override
    protected SemaphoreTryAcquireCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SemaphoreTryAcquireCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SemaphoreTryAcquireCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:semaphoreService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SemaphorePermission(((SemaphoreTryAcquireCodec.RequestParameters)this.parameters).name, "acquire");
    }

    @Override
    public String getDistributedObjectName() {
        return ((SemaphoreTryAcquireCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "tryAcquire";
    }

    @Override
    public Object[] getParameters() {
        if (((SemaphoreTryAcquireCodec.RequestParameters)this.parameters).timeout > 0L) {
            return new Object[]{((SemaphoreTryAcquireCodec.RequestParameters)this.parameters).permits, ((SemaphoreTryAcquireCodec.RequestParameters)this.parameters).timeout, TimeUnit.MILLISECONDS};
        }
        return new Object[]{((SemaphoreTryAcquireCodec.RequestParameters)this.parameters).permits};
    }
}

