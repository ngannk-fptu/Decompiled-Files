/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddRingbufferConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.config.RingbufferStoreConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class AddRingbufferConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddRingbufferConfigCodec.RequestParameters> {
    public AddRingbufferConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddRingbufferConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddRingbufferConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddRingbufferConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        RingbufferConfig config = new RingbufferConfig(((DynamicConfigAddRingbufferConfigCodec.RequestParameters)this.parameters).name);
        config.setAsyncBackupCount(((DynamicConfigAddRingbufferConfigCodec.RequestParameters)this.parameters).asyncBackupCount);
        config.setBackupCount(((DynamicConfigAddRingbufferConfigCodec.RequestParameters)this.parameters).backupCount);
        config.setCapacity(((DynamicConfigAddRingbufferConfigCodec.RequestParameters)this.parameters).capacity);
        config.setInMemoryFormat(InMemoryFormat.valueOf(((DynamicConfigAddRingbufferConfigCodec.RequestParameters)this.parameters).inMemoryFormat));
        config.setTimeToLiveSeconds(((DynamicConfigAddRingbufferConfigCodec.RequestParameters)this.parameters).timeToLiveSeconds);
        if (((DynamicConfigAddRingbufferConfigCodec.RequestParameters)this.parameters).ringbufferStoreConfig != null) {
            RingbufferStoreConfig storeConfig = ((DynamicConfigAddRingbufferConfigCodec.RequestParameters)this.parameters).ringbufferStoreConfig.asRingbufferStoreConfig(this.serializationService);
            config.setRingbufferStoreConfig(storeConfig);
        }
        MergePolicyConfig mergePolicyConfig = this.mergePolicyConfig(((DynamicConfigAddRingbufferConfigCodec.RequestParameters)this.parameters).mergePolicyExist, ((DynamicConfigAddRingbufferConfigCodec.RequestParameters)this.parameters).mergePolicy, ((DynamicConfigAddRingbufferConfigCodec.RequestParameters)this.parameters).mergeBatchSize);
        config.setMergePolicyConfig(mergePolicyConfig);
        return config;
    }

    @Override
    public String getMethodName() {
        return "addRingbufferConfig";
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        RingbufferConfig ringbufferConfig = (RingbufferConfig)config;
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getRingbufferConfigs(), ringbufferConfig.getName(), ringbufferConfig);
    }
}

