/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPSessionHeartbeatSessionCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.session.operation.HeartbeatSessionOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class HeartbeatSessionMessageTask
extends AbstractCPMessageTask<CPSessionHeartbeatSessionCodec.RequestParameters> {
    public HeartbeatSessionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.invoke(((CPSessionHeartbeatSessionCodec.RequestParameters)this.parameters).groupId, new HeartbeatSessionOp(((CPSessionHeartbeatSessionCodec.RequestParameters)this.parameters).sessionId));
    }

    @Override
    protected CPSessionHeartbeatSessionCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPSessionHeartbeatSessionCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPSessionHeartbeatSessionCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:core:raft";
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}

