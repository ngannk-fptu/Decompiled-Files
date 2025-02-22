/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddTopicConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class AddTopicConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddTopicConfigCodec.RequestParameters> {
    public AddTopicConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddTopicConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddTopicConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddTopicConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        TopicConfig config = new TopicConfig(((DynamicConfigAddTopicConfigCodec.RequestParameters)this.parameters).name);
        config.setGlobalOrderingEnabled(((DynamicConfigAddTopicConfigCodec.RequestParameters)this.parameters).globalOrderingEnabled);
        config.setMultiThreadingEnabled(((DynamicConfigAddTopicConfigCodec.RequestParameters)this.parameters).multiThreadingEnabled);
        config.setStatisticsEnabled(((DynamicConfigAddTopicConfigCodec.RequestParameters)this.parameters).statisticsEnabled);
        if (((DynamicConfigAddTopicConfigCodec.RequestParameters)this.parameters).listenerConfigs != null && !((DynamicConfigAddTopicConfigCodec.RequestParameters)this.parameters).listenerConfigs.isEmpty()) {
            config.setMessageListenerConfigs(this.adaptListenerConfigs(((DynamicConfigAddTopicConfigCodec.RequestParameters)this.parameters).listenerConfigs));
        }
        return config;
    }

    @Override
    public String getMethodName() {
        return "addTopicConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        TopicConfig topicConfig = (TopicConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getTopicConfigs(), topicConfig.getName(), topicConfig);
    }
}

