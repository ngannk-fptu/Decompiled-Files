/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddCardinalityEstimatorConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.CardinalityEstimatorConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class AddCardinalityEstimatorConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddCardinalityEstimatorConfigCodec.RequestParameters> {
    public AddCardinalityEstimatorConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddCardinalityEstimatorConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddCardinalityEstimatorConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddCardinalityEstimatorConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        CardinalityEstimatorConfig config = new CardinalityEstimatorConfig(((DynamicConfigAddCardinalityEstimatorConfigCodec.RequestParameters)this.parameters).name, ((DynamicConfigAddCardinalityEstimatorConfigCodec.RequestParameters)this.parameters).backupCount, ((DynamicConfigAddCardinalityEstimatorConfigCodec.RequestParameters)this.parameters).asyncBackupCount);
        if (((DynamicConfigAddCardinalityEstimatorConfigCodec.RequestParameters)this.parameters).mergePolicyExist) {
            MergePolicyConfig mergePolicyConfig = this.mergePolicyConfig(((DynamicConfigAddCardinalityEstimatorConfigCodec.RequestParameters)this.parameters).mergePolicyExist, ((DynamicConfigAddCardinalityEstimatorConfigCodec.RequestParameters)this.parameters).mergePolicy, ((DynamicConfigAddCardinalityEstimatorConfigCodec.RequestParameters)this.parameters).mergeBatchSize);
            config.setMergePolicyConfig(mergePolicyConfig);
        }
        return config;
    }

    @Override
    public String getMethodName() {
        return "addCardinalityEstimatorConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        CardinalityEstimatorConfig cardinalityEstimatorConfig = (CardinalityEstimatorConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getCardinalityEstimatorConfigs(), cardinalityEstimatorConfig.getName(), cardinalityEstimatorConfig);
    }
}

