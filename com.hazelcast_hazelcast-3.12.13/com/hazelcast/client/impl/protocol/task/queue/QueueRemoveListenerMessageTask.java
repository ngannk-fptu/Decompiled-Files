/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.queue;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueRemoveListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractRemoveListenerMessageTask;
import com.hazelcast.collection.impl.queue.QueueService;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.QueuePermission;
import java.security.Permission;

public class QueueRemoveListenerMessageTask
extends AbstractRemoveListenerMessageTask<QueueRemoveListenerCodec.RequestParameters> {
    public QueueRemoveListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean deRegisterListener() {
        QueueService service = (QueueService)this.getService(this.getServiceName());
        return service.removeItemListener(((QueueRemoveListenerCodec.RequestParameters)this.parameters).name, ((QueueRemoveListenerCodec.RequestParameters)this.parameters).registrationId);
    }

    @Override
    protected String getRegistrationId() {
        return ((QueueRemoveListenerCodec.RequestParameters)this.parameters).registrationId;
    }

    @Override
    protected QueueRemoveListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return QueueRemoveListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return QueueRemoveListenerCodec.encodeResponse((Boolean)response);
    }

    @Override
    public Permission getRequiredPermission() {
        return new QueuePermission(((QueueRemoveListenerCodec.RequestParameters)this.parameters).name, "listen");
    }

    @Override
    public String getMethodName() {
        return "removeItemListener";
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public String getDistributedObjectName() {
        return ((QueueRemoveListenerCodec.RequestParameters)this.parameters).name;
    }
}

