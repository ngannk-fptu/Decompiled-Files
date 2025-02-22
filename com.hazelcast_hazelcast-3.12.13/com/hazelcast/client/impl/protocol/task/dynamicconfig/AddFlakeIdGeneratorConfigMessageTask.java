/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddFlakeIdGeneratorConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.FlakeIdGeneratorConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class AddFlakeIdGeneratorConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddFlakeIdGeneratorConfigCodec.RequestParameters> {
    public AddFlakeIdGeneratorConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddFlakeIdGeneratorConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddFlakeIdGeneratorConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddFlakeIdGeneratorConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        FlakeIdGeneratorConfig config = new FlakeIdGeneratorConfig(((DynamicConfigAddFlakeIdGeneratorConfigCodec.RequestParameters)this.parameters).name);
        config.setPrefetchCount(((DynamicConfigAddFlakeIdGeneratorConfigCodec.RequestParameters)this.parameters).prefetchCount);
        config.setPrefetchValidityMillis(((DynamicConfigAddFlakeIdGeneratorConfigCodec.RequestParameters)this.parameters).prefetchValidity);
        config.setIdOffset(((DynamicConfigAddFlakeIdGeneratorConfigCodec.RequestParameters)this.parameters).idOffset);
        config.setNodeIdOffset(((DynamicConfigAddFlakeIdGeneratorConfigCodec.RequestParameters)this.parameters).nodeIdOffset);
        config.setStatisticsEnabled(((DynamicConfigAddFlakeIdGeneratorConfigCodec.RequestParameters)this.parameters).statisticsEnabled);
        return config;
    }

    @Override
    public String getMethodName() {
        return "addFlakeIdGeneratorConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        FlakeIdGeneratorConfig flakeIdGeneratorConfig = (FlakeIdGeneratorConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getFlakeIdGeneratorConfigs(), flakeIdGeneratorConfig.getName(), flakeIdGeneratorConfig);
    }
}

