/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPSemaphoreAcquireCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.AcquirePermitsOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SemaphorePermission;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class AcquirePermitsMessageTask
extends AbstractCPMessageTask<CPSemaphoreAcquireCodec.RequestParameters> {
    public AcquirePermitsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        AcquirePermitsOp op = new AcquirePermitsOp(((CPSemaphoreAcquireCodec.RequestParameters)this.parameters).name, ((CPSemaphoreAcquireCodec.RequestParameters)this.parameters).sessionId, ((CPSemaphoreAcquireCodec.RequestParameters)this.parameters).threadId, ((CPSemaphoreAcquireCodec.RequestParameters)this.parameters).invocationUid, ((CPSemaphoreAcquireCodec.RequestParameters)this.parameters).permits, ((CPSemaphoreAcquireCodec.RequestParameters)this.parameters).timeoutMs);
        this.invoke(((CPSemaphoreAcquireCodec.RequestParameters)this.parameters).groupId, op);
    }

    @Override
    protected CPSemaphoreAcquireCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPSemaphoreAcquireCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPSemaphoreAcquireCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:semaphoreService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SemaphorePermission(((CPSemaphoreAcquireCodec.RequestParameters)this.parameters).name, "acquire");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPSemaphoreAcquireCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return ((CPSemaphoreAcquireCodec.RequestParameters)this.parameters).timeoutMs >= 0L ? "tryAcquire" : "acquire";
    }

    @Override
    public Object[] getParameters() {
        if (((CPSemaphoreAcquireCodec.RequestParameters)this.parameters).timeoutMs > 0L) {
            return new Object[]{((CPSemaphoreAcquireCodec.RequestParameters)this.parameters).permits, ((CPSemaphoreAcquireCodec.RequestParameters)this.parameters).timeoutMs, TimeUnit.MILLISECONDS};
        }
        return new Object[]{((CPSemaphoreAcquireCodec.RequestParameters)this.parameters).permits};
    }
}

