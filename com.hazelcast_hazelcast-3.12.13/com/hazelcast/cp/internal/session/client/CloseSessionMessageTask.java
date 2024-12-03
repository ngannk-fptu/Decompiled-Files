/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPSessionCloseSessionCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.session.operation.CloseSessionOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class CloseSessionMessageTask
extends AbstractCPMessageTask<CPSessionCloseSessionCodec.RequestParameters> {
    public CloseSessionMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.invoke(((CPSessionCloseSessionCodec.RequestParameters)this.parameters).groupId, new CloseSessionOp(((CPSessionCloseSessionCodec.RequestParameters)this.parameters).sessionId));
    }

    @Override
    protected CPSessionCloseSessionCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPSessionCloseSessionCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPSessionCloseSessionCodec.encodeResponse((Boolean)response);
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

