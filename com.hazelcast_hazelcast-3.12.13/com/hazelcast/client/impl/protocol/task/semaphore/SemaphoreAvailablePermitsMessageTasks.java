/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.semaphore;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SemaphoreAvailablePermitsCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.semaphore.operations.AvailableOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SemaphorePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class SemaphoreAvailablePermitsMessageTasks
extends AbstractPartitionMessageTask<SemaphoreAvailablePermitsCodec.RequestParameters> {
    public SemaphoreAvailablePermitsMessageTasks(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new AvailableOperation(((SemaphoreAvailablePermitsCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected SemaphoreAvailablePermitsCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SemaphoreAvailablePermitsCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SemaphoreAvailablePermitsCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:semaphoreService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SemaphorePermission(((SemaphoreAvailablePermitsCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((SemaphoreAvailablePermitsCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "availablePermits";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

