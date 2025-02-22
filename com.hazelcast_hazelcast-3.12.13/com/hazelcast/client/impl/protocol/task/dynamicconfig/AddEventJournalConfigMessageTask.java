/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddEventJournalConfigCodec;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AbstractAddConfigMessageTask;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.dynamicconfig.DynamicConfigurationAwareConfig;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.StringUtil;

public class AddEventJournalConfigMessageTask
extends AbstractAddConfigMessageTask<DynamicConfigAddEventJournalConfigCodec.RequestParameters> {
    public AddEventJournalConfigMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected DynamicConfigAddEventJournalConfigCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return DynamicConfigAddEventJournalConfigCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return DynamicConfigAddEventJournalConfigCodec.encodeResponse();
    }

    @Override
    protected IdentifiedDataSerializable getConfig() {
        EventJournalConfig config = new EventJournalConfig();
        if (StringUtil.isNullOrEmpty(((DynamicConfigAddEventJournalConfigCodec.RequestParameters)this.parameters).mapName) && StringUtil.isNullOrEmpty(((DynamicConfigAddEventJournalConfigCodec.RequestParameters)this.parameters).cacheName)) {
            throw new IllegalArgumentException("Event journal config should have non-empty map name and/or cache name");
        }
        if (!StringUtil.isNullOrEmpty(((DynamicConfigAddEventJournalConfigCodec.RequestParameters)this.parameters).mapName)) {
            config.setMapName(((DynamicConfigAddEventJournalConfigCodec.RequestParameters)this.parameters).mapName);
        }
        if (!StringUtil.isNullOrEmpty(((DynamicConfigAddEventJournalConfigCodec.RequestParameters)this.parameters).cacheName)) {
            config.setCacheName(((DynamicConfigAddEventJournalConfigCodec.RequestParameters)this.parameters).cacheName);
        }
        config.setEnabled(((DynamicConfigAddEventJournalConfigCodec.RequestParameters)this.parameters).enabled);
        config.setTimeToLiveSeconds(((DynamicConfigAddEventJournalConfigCodec.RequestParameters)this.parameters).timeToLiveSeconds);
        config.setCapacity(((DynamicConfigAddEventJournalConfigCodec.RequestParameters)this.parameters).capacity);
        return config;
    }

    @Override
    protected boolean checkStaticConfigDoesNotExist(IdentifiedDataSerializable config) {
        DynamicConfigurationAwareConfig nodeConfig = (DynamicConfigurationAwareConfig)this.nodeEngine.getConfig();
        EventJournalConfig eventJournalConfig = (EventJournalConfig)config;
        String name = eventJournalConfig.getMapName();
        if (name == null) {
            name = eventJournalConfig.getCacheName();
        }
        return nodeConfig.checkStaticConfigDoesNotExist(nodeConfig.getStaticConfig().getCacheEventJournalConfigs(), name, eventJournalConfig);
    }

    @Override
    public String getMethodName() {
        return "addEventJournalConfig";
    }
}

