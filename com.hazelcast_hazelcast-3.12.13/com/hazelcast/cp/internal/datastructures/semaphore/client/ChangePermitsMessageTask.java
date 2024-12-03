/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPSemaphoreChangeCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.ChangePermitsOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SemaphorePermission;
import java.security.Permission;

public class ChangePermitsMessageTask
extends AbstractCPMessageTask<CPSemaphoreChangeCodec.RequestParameters> {
    public ChangePermitsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        ChangePermitsOp op = new ChangePermitsOp(((CPSemaphoreChangeCodec.RequestParameters)this.parameters).name, ((CPSemaphoreChangeCodec.RequestParameters)this.parameters).sessionId, ((CPSemaphoreChangeCodec.RequestParameters)this.parameters).threadId, ((CPSemaphoreChangeCodec.RequestParameters)this.parameters).invocationUid, ((CPSemaphoreChangeCodec.RequestParameters)this.parameters).permits);
        this.invoke(((CPSemaphoreChangeCodec.RequestParameters)this.parameters).groupId, op);
    }

    @Override
    protected CPSemaphoreChangeCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPSemaphoreChangeCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPSemaphoreChangeCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:semaphoreService";
    }

    @Override
    public Permission getRequiredPermission() {
        return ((CPSemaphoreChangeCodec.RequestParameters)this.parameters).permits < 0 ? new SemaphorePermission(((CPSemaphoreChangeCodec.RequestParameters)this.parameters).name, "acquire") : new SemaphorePermission(((CPSemaphoreChangeCodec.RequestParameters)this.parameters).name, "release");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPSemaphoreChangeCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return ((CPSemaphoreChangeCodec.RequestParameters)this.parameters).permits > 0 ? "increasePermits" : "reducePermits";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{Math.abs(((CPSemaphoreChangeCodec.RequestParameters)this.parameters).permits)};
    }
}

