/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddReplicatedMapConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.ListenerConfigHolder;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.util.ArrayList;

public class AddReplicatedMapConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddReplicatedMapConfigCodec.RequestParameters> {
    public AddReplicatedMapConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddReplicatedMapConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddReplicatedMapConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddReplicatedMapConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        ReplicatedMapConfig config = new ReplicatedMapConfig(((DynamicConfigAddReplicatedMapConfigCodec.RequestParameters)this.parameters).name);
        config.setAsyncFillup(((DynamicConfigAddReplicatedMapConfigCodec.RequestParameters)this.parameters).asyncFillup);
        config.setInMemoryFormat(InMemoryFormat.valueOf(((DynamicConfigAddReplicatedMapConfigCodec.RequestParameters)this.parameters).inMemoryFormat));
        if (((DynamicConfigAddReplicatedMapConfigCodec.RequestParameters)this.parameters).mergeBatchSizeExist) {
            MergePolicyConfig mergePolicyConfig = this.mergePolicyConfig(true, ((DynamicConfigAddReplicatedMapConfigCodec.RequestParameters)this.parameters).mergePolicy, ((DynamicConfigAddReplicatedMapConfigCodec.RequestParameters)this.parameters).mergeBatchSize);
            config.setMergePolicyConfig(mergePolicyConfig);
        }
        config.setStatisticsEnabled(((DynamicConfigAddReplicatedMapConfigCodec.RequestParameters)this.parameters).statisticsEnabled);
        if (((DynamicConfigAddReplicatedMapConfigCodec.RequestParameters)this.parameters).listenerConfigs != null && !((DynamicConfigAddReplicatedMapConfigCodec.RequestParameters)this.parameters).listenerConfigs.isEmpty()) {
            for (ListenerConfigHolder holder : ((DynamicConfigAddReplicatedMapConfigCodec.RequestParameters)this.parameters).listenerConfigs) {
                config.addEntryListenerConfig((EntryListenerConfig)holder.asListenerConfig(this.serializationService));
            }
        } else {
            config.setListenerConfigs(new ArrayList<ListenerConfig>());
        }
        return config;
    }

    @Override
    public String getMethodName() {
        return "addReplicatedMapConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        ReplicatedMapConfig replicatedMapConfig = (ReplicatedMapConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getReplicatedMapConfigs(), replicatedMapConfig.getName(), replicatedMapConfig);
    }
}

