/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddExecutorConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class AddExecutorConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddExecutorConfigCodec.RequestParameters> {
    public AddExecutorConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddExecutorConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddExecutorConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddExecutorConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        ExecutorConfig config = new ExecutorConfig(((DynamicConfigAddExecutorConfigCodec.RequestParameters)this.parameters).name, ((DynamicConfigAddExecutorConfigCodec.RequestParameters)this.parameters).poolSize);
        config.setQueueCapacity(((DynamicConfigAddExecutorConfigCodec.RequestParameters)this.parameters).queueCapacity);
        config.setStatisticsEnabled(((DynamicConfigAddExecutorConfigCodec.RequestParameters)this.parameters).statisticsEnabled);
        return config;
    }

    @Override
    public String getMethodName() {
        return "addExecutorConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        ExecutorConfig executorConfig = (ExecutorConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getExecutorConfigs(), executorConfig.getName(), executorConfig);
    }
}

