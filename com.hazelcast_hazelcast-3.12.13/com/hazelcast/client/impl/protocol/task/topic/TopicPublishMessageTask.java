/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.topic;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.TopicPublishCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.security.permission.TopicPermission;
import com.hazelcast.spi.Operation;
import com.hazelcast.topic.impl.PublishOperation;
import java.security.Permission;

public class TopicPublishMessageTask
extends AbstractPartitionMessageTask<TopicPublishCodec.RequestParameters> {
    public TopicPublishMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new PublishOperation(((TopicPublishCodec.RequestParameters)this.parameters).name, ((TopicPublishCodec.RequestParameters)this.parameters).message);
    }

    @Override
    protected TopicPublishCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return TopicPublishCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return TopicPublishCodec.encodeResponse();
    }

    @Override
    public String getServiceName() {
        return "hz:impl:topicService";
    }

    @Override
    public Permission getRequiredPermission() {
        return new TopicPermission(((TopicPublishCodec.RequestParameters)this.parameters).name, "publish");
    }

    @Override
    public String getDistributedObjectName() {
        return ((TopicPublishCodec.RequestParameters)this.parameters).name;
    }

    @Override
    public String getMethodName() {
        return "publish";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{((TopicPublishCodec.RequestParameters)this.parameters).message};
    }
}

