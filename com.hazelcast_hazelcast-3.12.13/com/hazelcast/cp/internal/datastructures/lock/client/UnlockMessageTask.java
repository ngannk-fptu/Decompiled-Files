/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPFencedLockUnlockCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.lock.operation.UnlockOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.LockPermission;
import java.security.Permission;

public class UnlockMessageTask
extends AbstractCPMessageTask<CPFencedLockUnlockCodec.RequestParameters> {
    public UnlockMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        UnlockOp op = new UnlockOp(((CPFencedLockUnlockCodec.RequestParameters)this.parameters).name, ((CPFencedLockUnlockCodec.RequestParameters)this.parameters).sessionId, ((CPFencedLockUnlockCodec.RequestParameters)this.parameters).threadId, ((CPFencedLockUnlockCodec.RequestParameters)this.parameters).invocationUid);
        this.invoke(((CPFencedLockUnlockCodec.RequestParameters)this.parameters).groupId, op);
    }

    @Override
    protected CPFencedLockUnlockCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPFencedLockUnlockCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPFencedLockUnlockCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:lockService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(((CPFencedLockUnlockCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPFencedLockUnlockCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "unlock";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}

