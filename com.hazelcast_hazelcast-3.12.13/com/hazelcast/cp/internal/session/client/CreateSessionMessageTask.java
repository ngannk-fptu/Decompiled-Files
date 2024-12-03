/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPSessionCreateSessionCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.session.SessionResponse;
import com.hazelcast.cp.internal.session.operation.CreateSessionOp;
import com.hazelcast.cp.session.CPSession;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class CreateSessionMessageTask
extends AbstractCPMessageTask<CPSessionCreateSessionCodec.RequestParameters> {
    public CreateSessionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        CreateSessionOp op = new CreateSessionOp(this.connection.getEndPoint(), ((CPSessionCreateSessionCodec.RequestParameters)this.parameters).endpointName, CPSession.CPSessionOwnerType.CLIENT);
        this.invoke(((CPSessionCreateSessionCodec.RequestParameters)this.parameters).groupId, op);
    }

    @Override
    protected CPSessionCreateSessionCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPSessionCreateSessionCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        SessionResponse session = (SessionResponse)response;
        return CPSessionCreateSessionCodec.encodeResponse(session.getSessionId(), session.getTtlMillis(), session.getHeartbeatMillis());
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
        return "create";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }
}

