/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.topic;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TopicRemoveMessageListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractRemoveListenerMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.TopicPermission;
import com.hazelcast.topic.impl.TopicService;
import java.security.Permission;

public class TopicRemoveMessageListenerMessageTask
extends AbstractRemoveListenerMessageTask<TopicRemoveMessageListenerCodec.RequestParameters> {
    public TopicRemoveMessageListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected boolean deRegisterListener() {
        TopicService service = (TopicService)this.getService("hz:impl:topicService");
        return service.removeMessageListener(((TopicRemoveMessageListenerCodec.RequestParameters)this.parameters).name, ((TopicRemoveMessageListenerCodec.RequestParameters)this.parameters).registrationId);
    }

    @Override
    protected String getRegistrationId() {
        return ((TopicRemoveMessageListenerCodec.RequestParameters)this.parameters).registrationId;
    }

    @Override
    protected TopicRemoveMessageListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TopicRemoveMessageListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TopicRemoveMessageListenerCodec.encodeResponse((Boolean)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:topicService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new TopicPermission(((TopicRemoveMessageListenerCodec.RequestParameters)this.parameters).name, "listen");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TopicRemoveMessageListenerCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "removeMessageListener";
    }
}

