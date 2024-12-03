/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.semaphore;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SemaphoreReleaseCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.semaphore.operations.ReleaseOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SemaphorePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class SemaphoreReleaseMessageTask
extends AbstractPartitionMessageTask<SemaphoreReleaseCodec.RequestParameters> {
    public SemaphoreReleaseMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ReleaseOperation(((SemaphoreReleaseCodec.RequestParameters)this.parameters).name, ((SemaphoreReleaseCodec.RequestParameters)this.parameters).permits);
    }

    @Override
    protected SemaphoreReleaseCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SemaphoreReleaseCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SemaphoreReleaseCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:semaphoreService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SemaphorePermission(((SemaphoreReleaseCodec.RequestParameters)this.parameters).name, "release");
    }

    @Override
    public String getDistributedObjectName() {
        return ((SemaphoreReleaseCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "release";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((SemaphoreReleaseCodec.RequestParameters)this.parameters).permits};
    }
}

