/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session.client;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.CPSessionGenerateThreadIdCodec;
import com.hazelcast.cp.internal.client.AbstractCPMessageTask;
import com.hazelcast.cp.internal.session.operation.GenerateThreadIdOp;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class GenerateThreadIdMessageTask
extends AbstractCPMessageTask<CPSessionGenerateThreadIdCodec.RequestParameters> {
    public GenerateThreadIdMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected void processMessage() {
        this.invoke(((CPSessionGenerateThreadIdCodec.RequestParameters)this.parameters).groupId, new GenerateThreadIdOp());
    }

    @Override
    protected CPSessionGenerateThreadIdCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return CPSessionGenerateThreadIdCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return CPSessionGenerateThreadIdCodec.encodeResponse((Long)response);
    }

    @Override
    public String getServiceName() {
        return "hz:raft:semaphoreService";
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

