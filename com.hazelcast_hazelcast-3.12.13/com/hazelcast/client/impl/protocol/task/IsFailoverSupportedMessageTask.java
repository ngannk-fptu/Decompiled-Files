/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientIsFailoverSupportedCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class IsFailoverSupportedMessageTask
extends AbstractCallableMessageTask<ClientIsFailoverSupportedCodec.RequestParameters> {
    public IsFailoverSupportedMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected ClientIsFailoverSupportedCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ClientIsFailoverSupportedCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ClientIsFailoverSupportedCodec.encodeResponse((Boolean)response);
    }

    @Override
    protected Object call() throws Exception {
        return this.nodeEngine.getNode().getNodeExtension().isClientFailoverSupported();
    }

    @Override
    protected boolean requiresAuthentication() {
        return false;
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

    @Override
    public String getDistributedObjectName() {
        return null;
    }

    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return null;
    }
}

