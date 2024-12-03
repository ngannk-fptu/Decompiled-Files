/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListRemoveListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractRemoveListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.EventService;
import java.security.Permission;

public class ListRemoveListenerMessageTask
extends AbstractRemoveListenerMessageTask<ListRemoveListenerCodec.RequestParameters> {
    public ListRemoveListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean deRegisterListener() {
        EventService eventService = this.clientEngine.getEventService();
        return eventService.deregisterListener(this.getServiceName(), ((ListRemoveListenerCodec.RequestParameters)this.parameters).name, ((ListRemoveListenerCodec.RequestParameters)this.parameters).registrationId);
    }

    @Override
    protected String getRegistrationId() {
        return ((ListRemoveListenerCodec.RequestParameters)this.parameters).registrationId;
    }

    @Override
    protected ListRemoveListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListRemoveListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListRemoveListenerCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(((ListRemoveListenerCodec.RequestParameters)this.parameters).name, "listen");
    }

    @Override
    public String getMethodName() {
        return "removeItemListener";
    }

    @Override
    public String getDistributedObjectName() {
        return ((ListRemoveListenerCodec.RequestParameters)this.parameters).name;
    }
}

