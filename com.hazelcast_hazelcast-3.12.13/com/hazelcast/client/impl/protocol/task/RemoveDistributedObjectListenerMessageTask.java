/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientRemoveDistributedObjectListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractRemoveListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import java.security.Permission;

public class RemoveDistributedObjectListenerMessageTask
extends AbstractRemoveListenerMessageTask<ClientRemoveDistributedObjectListenerCodec.RequestParameters> {
    public RemoveDistributedObjectListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean deRegisterListener() {
        return this.clientEngine.getProxyService().removeProxyListener(((ClientRemoveDistributedObjectListenerCodec.RequestParameters)this.parameters).registrationId);
    }

    @Override
    protected String getRegistrationId() {
        return ((ClientRemoveDistributedObjectListenerCodec.RequestParameters)this.parameters).registrationId;
    }

    @Override
    protected ClientRemoveDistributedObjectListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ClientRemoveDistributedObjectListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ClientRemoveDistributedObjectListenerCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:core:proxyService";
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
        return "removeDistributedObjectListener";
    }
}

