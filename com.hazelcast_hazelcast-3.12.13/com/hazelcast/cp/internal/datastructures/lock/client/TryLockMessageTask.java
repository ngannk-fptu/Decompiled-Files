/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPFencedLockTryLockCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.LockPermission;
import java.security.Permission;
import java.util.concurrent.TimeUnit;

public class TryLockMessageTask
extends AbstractCPMessageTask<CPFencedLockTryLockCodec.RequestParameters> {
    public TryLockMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        TryLockOp op = new TryLockOp(((CPFencedLockTryLockCodec.RequestParameters)this.parameters).name, ((CPFencedLockTryLockCodec.RequestParameters)this.parameters).sessionId, ((CPFencedLockTryLockCodec.RequestParameters)this.parameters).threadId, ((CPFencedLockTryLockCodec.RequestParameters)this.parameters).invocationUid, ((CPFencedLockTryLockCodec.RequestParameters)this.parameters).timeoutMs);
        this.invoke(((CPFencedLockTryLockCodec.RequestParameters)this.parameters).groupId, op);
    }

    @Override
    protected CPFencedLockTryLockCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPFencedLockTryLockCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPFencedLockTryLockCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:lockService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(((CPFencedLockTryLockCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPFencedLockTryLockCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "tryLock";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((CPFencedLockTryLockCodec.RequestParameters)this.parameters).timeoutMs, TimeUnit.MILLISECONDS};
    }
}

