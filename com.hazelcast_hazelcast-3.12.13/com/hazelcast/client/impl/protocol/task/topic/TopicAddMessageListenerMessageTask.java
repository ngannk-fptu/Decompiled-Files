/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.topic;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TopicAddMessageListenerCodec;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.client.impl.protocol.task.ListenerMessageTask;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.TopicPermission;
import com.hazelcast.topic.impl.DataAwareMessage;
import com.hazelcast.topic.impl.TopicService;
import com.hazelcast.util.HashUtil;
import java.security.Permission;
import java.util.Random;

public class TopicAddMessageListenerMessageTask
extends AbstractCallableMessageTask<TopicAddMessageListenerCodec.RequestParameters>
implements MessageListener,
ListenerMessageTask {
    private Data partitionKey;
    private Random rand = new Random();

    public TopicAddMessageListenerMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Object call() throws Exception {
        this.partitionKey = this.serializationService.toData(((TopicAddMessageListenerCodec.RequestParameters)this.parameters).name);
        TopicService service = (TopicService)this.getService("hz:impl:topicService");
        String registrationId = service.addMessageListener(((TopicAddMessageListenerCodec.RequestParameters)this.parameters).name, this, ((TopicAddMessageListenerCodec.RequestParameters)this.parameters).localOnly);
        this.endpoint.addListenerDestroyAction("hz:impl:topicService", ((TopicAddMessageListenerCodec.RequestParameters)this.parameters).name, registrationId);
        return registrationId;
    }

    @Override
    protected TopicAddMessageListenerCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TopicAddMessageListenerCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TopicAddMessageListenerCodec.encodeResponse((String)response);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:topicService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new TopicPermission(((TopicAddMessageListenerCodec.RequestParameters)this.parameters).name, "listen");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TopicAddMessageListenerCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "addMessageListener";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{null};
    }

    public void onMessage(Message message) {
        if (!this.endpoint.isAlive()) {
            return;
        }
        if (!(message instanceof DataAwareMessage)) {
            throw new IllegalArgumentException("Expecting: DataAwareMessage, Found: " + message.getClass().getSimpleName());
        }
        DataAwareMessage dataAwareMessage = (DataAwareMessage)message;
        Data messageData = dataAwareMessage.getMessageData();
        String publisherUuid = message.getPublishingMember().getUuid();
        ClientMessage eventMessage = TopicAddMessageListenerCodec.encodeTopicEvent(messageData, message.getPublishTime(), publisherUuid);
        boolean isMultithreaded = this.nodeEngine.getConfig().findTopicConfig(((TopicAddMessageListenerCodec.RequestParameters)this.parameters).name).isMultiThreadingEnabled();
        if (isMultithreaded) {
            int key = this.rand.nextInt();
            int partitionId = HashUtil.hashToIndex(key, this.nodeEngine.getPartitionService().getPartitionCount());
            eventMessage.setPartitionId(partitionId);
            this.sendClientMessage(eventMessage);
        } else {
            this.sendClientMessage(this.partitionKey, eventMessage);
        }
    }
}

