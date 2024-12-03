/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.semaphore;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SemaphoreIncreasePermitsCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.semaphore.operations.IncreaseOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SemaphorePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class SemaphoreIncreasePermitsMessageTask
extends AbstractPartitionMessageTask<SemaphoreIncreasePermitsCodec.RequestParameters> {
    public SemaphoreIncreasePermitsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new IncreaseOperation(((SemaphoreIncreasePermitsCodec.RequestParameters)this.parameters).name, ((SemaphoreIncreasePermitsCodec.RequestParameters)this.parameters).increase);
    }

    @Override
    protected SemaphoreIncreasePermitsCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SemaphoreIncreasePermitsCodec.decodeRequest(clientMessage);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:semaphoreService";
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SemaphoreIncreasePermitsCodec.encodeResponse();
    }

    @Override
    public Permission getRequiredPermission() {
        return new SemaphorePermission(((SemaphoreIncreasePermitsCodec.RequestParameters)this.parameters).name, "acquire");
    }

    @Override
    public String getDistributedObjectName() {
        return ((SemaphoreIncreasePermitsCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "increasePermits";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((SemaphoreIncreasePermitsCodec.RequestParameters)this.parameters).increase};
    }
}

