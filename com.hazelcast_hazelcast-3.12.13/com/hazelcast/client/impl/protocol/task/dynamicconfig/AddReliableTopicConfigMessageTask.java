/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddReliableTopicConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.ReliableTopicConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.topic.TopicOverloadPolicy;
import java.util.concurrent.Executor;

public class AddReliableTopicConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddReliableTopicConfigCodec.RequestParameters> {
    public AddReliableTopicConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddReliableTopicConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddReliableTopicConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddReliableTopicConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        ReliableTopicConfig config = new ReliableTopicConfig(((DynamicConfigAddReliableTopicConfigCodec.RequestParameters)this.parameters).name);
        config.setStatisticsEnabled(((DynamicConfigAddReliableTopicConfigCodec.RequestParameters)this.parameters).statisticsEnabled);
        config.setReadBatchSize(((DynamicConfigAddReliableTopicConfigCodec.RequestParameters)this.parameters).readBatchSize);
        config.setTopicOverloadPolicy(TopicOverloadPolicy.valueOf(((DynamicConfigAddReliableTopicConfigCodec.RequestParameters)this.parameters).topicOverloadPolicy));
        Executor executor = (Executor)this.serializationService.toObject(((DynamicConfigAddReliableTopicConfigCodec.RequestParameters)this.parameters).executor);
        config.setExecutor(executor);
        if (((DynamicConfigAddReliableTopicConfigCodec.RequestParameters)this.parameters).listenerConfigs != null && !((DynamicConfigAddReliableTopicConfigCodec.RequestParameters)this.parameters).listenerConfigs.isEmpty()) {
            config.setMessageListenerConfigs(this.adaptListenerConfigs(((DynamicConfigAddReliableTopicConfigCodec.RequestParameters)this.parameters).listenerConfigs));
        }
        return config;
    }

    @Override
    public String getMethodName() {
        return "addReliableTopicConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        ReliableTopicConfig reliableTopicConfig = (ReliableTopicConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getReliableTopicConfigs(), reliableTopicConfig.getName(), reliableTopicConfig);
    }
}

