/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientRemoveAllListenersCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class RemoveAllListenersMessageTask
extends AbstractCallableMessageTask<ClientRemoveAllListenersCodec.RequestParameters>
implements ListenerMessageTask {
    public RemoveAllListenersMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        this.endpoint.clearAllListeners();
        return null;
    }

    @Override
    protected ClientRemoveAllListenersCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ClientRemoveAllListenersCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ClientRemoveAllListenersCodec.encodeResponse();
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

