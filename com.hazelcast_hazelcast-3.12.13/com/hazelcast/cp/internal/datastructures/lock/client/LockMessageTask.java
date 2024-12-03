/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPFencedLockLockCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.lock.operation.LockOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.LockPermission;
import java.security.Permission;

public class LockMessageTask
extends AbstractCPMessageTask<CPFencedLockLockCodec.RequestParameters> {
    public LockMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.invoke(((CPFencedLockLockCodec.RequestParameters)this.parameters).groupId, new LockOp(((CPFencedLockLockCodec.RequestParameters)this.parameters).name, ((CPFencedLockLockCodec.RequestParameters)this.parameters).sessionId, ((CPFencedLockLockCodec.RequestParameters)this.parameters).threadId, ((CPFencedLockLockCodec.RequestParameters)this.parameters).invocationUid));
    }

    @Override
    protected CPFencedLockLockCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPFencedLockLockCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPFencedLockLockCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:lockService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(((CPFencedLockLockCodec.RequestParameters)this.parameters).name, "lock");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPFencedLockLockCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "lock";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}

