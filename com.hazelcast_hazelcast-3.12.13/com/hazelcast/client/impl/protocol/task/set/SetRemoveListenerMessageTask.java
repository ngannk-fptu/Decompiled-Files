/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.set;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.SetRemoveListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractRemoveListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.spi.EventService;
import java.security.Permission;

public class SetRemoveListenerMessageTask
extends AbstractRemoveListenerMessageTask<SetRemoveListenerCodec.RequestParameters> {
    public SetRemoveListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean deRegisterListener() {
        EventService eventService = this.clientEngine.getEventService();
        return eventService.deregisterListener(this.getServiceName(), ((SetRemoveListenerCodec.RequestParameters)this.parameters).name, ((SetRemoveListenerCodec.RequestParameters)this.parameters).registrationId);
    }

    @Override
    protected String getRegistrationId() {
        return ((SetRemoveListenerCodec.RequestParameters)this.parameters).registrationId;
    }

    @Override
    protected SetRemoveListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return SetRemoveListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return SetRemoveListenerCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:setService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new SetPermission(((SetRemoveListenerCodec.RequestParameters)this.parameters).name, "listen");
    }

    @Override
    public String getMethodName() {
        return "removeItemListener";
    }

    @Override
    public String getDistributedObjectName() {
        return ((SetRemoveListenerCodec.RequestParameters)this.parameters).name;
    }
}

