/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.lock.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPFencedLockGetLockOwnershipCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.lock.RaftLockOwnershipState;
import com.hazelcast.cp.internal.datastructures.lock.operation.GetLockOwnershipStateOp;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.LockPermission;
import java.security.Permission;

public class GetLockOwnershipStateMessageTask
extends AbstractCPMessageTask<CPFencedLockGetLockOwnershipCodec.RequestParameters> {
    public GetLockOwnershipStateMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.query(((CPFencedLockGetLockOwnershipCodec.RequestParameters)this.parameters).groupId, new GetLockOwnershipStateOp(((CPFencedLockGetLockOwnershipCodec.RequestParameters)this.parameters).name), QueryPolicy.LINEARIZABLE);
    }

    @Override
    protected CPFencedLockGetLockOwnershipCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPFencedLockGetLockOwnershipCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        RaftLockOwnershipState lockState = (RaftLockOwnershipState)response;
        return CPFencedLockGetLockOwnershipCodec.encodeResponse(lockState.getFence(), lockState.getLockCount(), lockState.getSessionId(), lockState.getThreadId());
    }

    @Override
    public String getServiceName() {
        return "hz:raft:lockService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new LockPermission(((CPFencedLockGetLockOwnershipCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPFencedLockGetLockOwnershipCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "getLockOwnershipState";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}

