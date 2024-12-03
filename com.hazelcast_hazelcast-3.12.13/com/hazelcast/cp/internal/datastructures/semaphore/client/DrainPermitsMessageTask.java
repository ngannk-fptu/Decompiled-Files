/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPSemaphoreDrainCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.DrainPermitsOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SemaphorePermission;
import java.security.Permission;

public class DrainPermitsMessageTask
extends AbstractCPMessageTask<CPSemaphoreDrainCodec.RequestParameters> {
    public DrainPermitsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        DrainPermitsOp op = new DrainPermitsOp(((CPSemaphoreDrainCodec.RequestParameters)this.parameters).name, ((CPSemaphoreDrainCodec.RequestParameters)this.parameters).sessionId, ((CPSemaphoreDrainCodec.RequestParameters)this.parameters).threadId, ((CPSemaphoreDrainCodec.RequestParameters)this.parameters).invocationUid);
        this.invoke(((CPSemaphoreDrainCodec.RequestParameters)this.parameters).groupId, op);
    }

    @Override
    protected CPSemaphoreDrainCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPSemaphoreDrainCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPSemaphoreDrainCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:semaphoreService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SemaphorePermission(((CPSemaphoreDrainCodec.RequestParameters)this.parameters).name, "acquire");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPSemaphoreDrainCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "drainPermits";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}

