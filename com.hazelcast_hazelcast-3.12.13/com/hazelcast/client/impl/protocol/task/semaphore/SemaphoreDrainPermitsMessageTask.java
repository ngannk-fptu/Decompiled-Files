/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.semaphore;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SemaphoreDrainPermitsCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.concurrent.semaphore.operations.DrainOperation;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SemaphorePermission;
import com.hazelcast.spi.Operation;
import java.security.Permission;

public class SemaphoreDrainPermitsMessageTask
extends AbstractPartitionMessageTask<SemaphoreDrainPermitsCodec.RequestParameters> {
    public SemaphoreDrainPermitsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new DrainOperation(((SemaphoreDrainPermitsCodec.RequestParameters)this.parameters).name);
    }

    @Override
    protected SemaphoreDrainPermitsCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SemaphoreDrainPermitsCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SemaphoreDrainPermitsCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:semaphoreService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SemaphorePermission(((SemaphoreDrainPermitsCodec.RequestParameters)this.parameters).name, "acquire");
    }

    @Override
    public String getDistributedObjectName() {
        return ((SemaphoreDrainPermitsCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "drainPermits";
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

