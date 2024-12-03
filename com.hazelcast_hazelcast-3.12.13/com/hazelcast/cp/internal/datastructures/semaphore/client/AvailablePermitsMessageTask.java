/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPSemaphoreAvailablePermitsCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.AvailablePermitsOp;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SemaphorePermission;
import java.security.Permission;

public class AvailablePermitsMessageTask
extends AbstractCPMessageTask<CPSemaphoreAvailablePermitsCodec.RequestParameters> {
    public AvailablePermitsMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.query(((CPSemaphoreAvailablePermitsCodec.RequestParameters)this.parameters).groupId, new AvailablePermitsOp(((CPSemaphoreAvailablePermitsCodec.RequestParameters)this.parameters).name), QueryPolicy.LINEARIZABLE);
    }

    @Override
    protected CPSemaphoreAvailablePermitsCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPSemaphoreAvailablePermitsCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPSemaphoreAvailablePermitsCodec.encodeResponse((Integer)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:semaphoreService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SemaphorePermission(((CPSemaphoreAvailablePermitsCodec.RequestParameters)this.parameters).name, "read");
    }

    @Override
    public String getDistributedObjectName() {
        return ((CPSemaphoreAvailablePermitsCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "availablePermits";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}

