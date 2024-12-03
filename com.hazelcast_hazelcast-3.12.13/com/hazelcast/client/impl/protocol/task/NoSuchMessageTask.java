/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.task.AbstractMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class NoSuchMessageTask
extends AbstractMessageTask<ClientMessage> {
    public NoSuchMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected ClientMessage decodeClientMessage(ClientMessage clientMessage) {
        return clientMessage;
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return null;
    }

    @Override
    protected void processMessage() {
        String message = "Unrecognized client message received with type: 0x" + Integer.toHexString(((ClientMessage)this.parameters).getMessageType());
        this.logger.warning(message);
        throw new UnsupportedOperationException(message);
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

    @Override
    public int getPartitionId() {
        return -1;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }
}

