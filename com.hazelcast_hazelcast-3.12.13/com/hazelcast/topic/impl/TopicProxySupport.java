/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.topic.impl;

import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.MessageListener;
import com.hazelcast.monitor.LocalTopicStats;
import com.hazelcast.monitor.impl.LocalTopicStatsImpl;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InitializingObject;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.topic.impl.TopicService;
import com.hazelcast.util.ExceptionUtil;

public abstract class TopicProxySupport
extends AbstractDistributedObject<TopicService>
implements InitializingObject {
    private final String name;
    private final ClassLoader configClassLoader;
    private final TopicService topicService;
    private final LocalTopicStatsImpl topicStats;
    private boolean multithreaded;

    public TopicProxySupport(String name, NodeEngine nodeEngine, TopicService service) {
        super(nodeEngine, service);
        this.name = name;
        this.configClassLoader = nodeEngine.getConfigClassLoader();
        this.topicService = service;
        this.topicStats = this.topicService.getLocalTopicStats(name);
    }

    @Override
    public void initialize() {
        NodeEngine nodeEngine = this.getNodeEngine();
        TopicConfig config = nodeEngine.getConfig().findTopicConfig(this.name);
        this.multithreaded = config.isMultiThreadingEnabled();
        for (ListenerConfig listenerConfig : config.getMessageListenerConfigs()) {
            this.initialize(listenerConfig);
        }
    }

    private void initialize(ListenerConfig listenerConfig) {
        NodeEngine nodeEngine = this.getNodeEngine();
        MessageListener listener = this.loadListener(listenerConfig);
        if (listener == null) {
            return;
        }
        if (listener instanceof HazelcastInstanceAware) {
            HazelcastInstanceAware hazelcastInstanceAware = (HazelcastInstanceAware)((Object)listener);
            hazelcastInstanceAware.setHazelcastInstance(nodeEngine.getHazelcastInstance());
        }
        this.addMessageListenerInternal(listener);
    }

    private MessageListener loadListener(ListenerConfig listenerConfig) {
        try {
            MessageListener listener = (MessageListener)listenerConfig.getImplementation();
            if (listener == null && listenerConfig.getClassName() != null) {
                listener = (MessageListener)ClassLoaderUtil.newInstance(this.configClassLoader, listenerConfig.getClassName());
            }
            return listener;
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    public LocalTopicStats getLocalTopicStatsInternal() {
        return this.topicService.getLocalTopicStats(this.name);
    }

    public void publishInternal(Object message) {
        this.topicStats.incrementPublishes();
        this.topicService.publishMessage(this.name, message, this.multithreaded);
    }

    public String addMessageListenerInternal(MessageListener listener) {
        return this.topicService.addMessageListener(this.name, listener, false);
    }

    public boolean removeMessageListenerInternal(String registrationId) {
        return this.topicService.removeMessageListener(this.name, registrationId);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:topicService";
    }

    @Override
    public String getName() {
        return this.name;
    }
}

