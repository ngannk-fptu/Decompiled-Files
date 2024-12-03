/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddPNCounterConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.PNCounterConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class AddPNCounterConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddPNCounterConfigCodec.RequestParameters> {
    public AddPNCounterConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddPNCounterConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddPNCounterConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddPNCounterConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        PNCounterConfig config = new PNCounterConfig(((DynamicConfigAddPNCounterConfigCodec.RequestParameters)this.parameters).name);
        config.setReplicaCount(((DynamicConfigAddPNCounterConfigCodec.RequestParameters)this.parameters).replicaCount);
        config.setStatisticsEnabled(((DynamicConfigAddPNCounterConfigCodec.RequestParameters)this.parameters).statisticsEnabled);
        config.setQuorumName(((DynamicConfigAddPNCounterConfigCodec.RequestParameters)this.parameters).quorumName);
        return config;
    }

    @Override
    public String getMethodName() {
        return "addPNCounterConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        PNCounterConfig pnCounterConfig = (PNCounterConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getPNCounterConfigs(), pnCounterConfig.getName(), pnCounterConfig);
    }
}

