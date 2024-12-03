/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.TopicConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TopicConfigReadOnly
extends TopicConfig {
    public TopicConfigReadOnly(TopicConfig config) {
        super(config);
    }

    @Override
    public List<ListenerConfig> getMessageListenerConfigs() {
        List<ListenerConfig> messageListenerConfigs = super.getMessageListenerConfigs();
        ArrayList<ListenerConfig> readOnlyMessageListenerConfigs = new ArrayList<ListenerConfig>(messageListenerConfigs.size());
        for (ListenerConfig messageListenerConfig : messageListenerConfigs) {
            readOnlyMessageListenerConfigs.add(messageListenerConfig.getAsReadOnly());
        }
        return Collections.unmodifiableList(readOnlyMessageListenerConfigs);
    }

    @Override
    public TopicConfig setName(String name) {
        throw new UnsupportedOperationException("This config is read-only topic: " + this.getName());
    }

    @Override
    public TopicConfig setGlobalOrderingEnabled(boolean globalOrderingEnabled) {
        throw new UnsupportedOperationException("This config is read-only topic: " + this.getName());
    }

    @Override
    public TopicConfig setMultiThreadingEnabled(boolean multiThreadingEnabled) {
        throw new UnsupportedOperationException("This config is read-only topic: " + this.getName());
    }

    @Override
    public TopicConfig addMessageListenerConfig(ListenerConfig listenerConfig) {
        throw new UnsupportedOperationException("This config is read-only topic: " + this.getName());
    }

    @Override
    public TopicConfig setMessageListenerConfigs(List<ListenerConfig> listenerConfigs) {
        throw new UnsupportedOperationException("This config is read-only topic: " + this.getName());
    }

    @Override
    public TopicConfig setStatisticsEnabled(boolean statisticsEnabled) {
        throw new UnsupportedOperationException("This config is read-only topic: " + this.getName());
    }
}

