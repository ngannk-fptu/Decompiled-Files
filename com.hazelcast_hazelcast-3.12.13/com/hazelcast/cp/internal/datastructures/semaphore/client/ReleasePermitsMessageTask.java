/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPSemaphoreReleaseCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.ReleasePermitsOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SemaphorePermission;
import java.security.Permission;

public class ReleasePermitsMessageTask
extends AbstractCPMessageTask<CPSemaphoreReleaseCodec.RequestParameters> {
    public ReleasePermitsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        ReleasePermitsOp op = new ReleasePermitsOp(((CPSemaphoreReleaseCodec.RequestParameters)this.parameters).name, ((CPSemaphoreReleaseCodec.RequestParameters)this.parameters).sessionId, ((CPSemaphoreReleaseCodec.RequestParameters)this.parameters).threadId, ((CPSemaphoreReleaseCodec.RequestParameters)this.parameters).invocationUid, ((CPSemaphoreReleaseCodec.RequestParameters)this.parameters).permits);
        this.invoke(((CPSemaphoreReleaseCodec.RequestParameters)this.parameters).groupId, op);
    }

    @Override
    protected CPSemaphoreReleaseCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPSemaphoreReleaseCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPSemaphoreReleaseCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:semaphoreService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SemaphorePermission(((CPSemaphoreReleaseCodec.RequestParameters)this.parameters).name, "release");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPSemaphoreReleaseCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "release";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPSemaphoreReleaseCodec.RequestParameters)this.parameters).permits};
    }
}

