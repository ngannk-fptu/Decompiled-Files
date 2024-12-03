/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.topic.impl;

import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;
import com.hazelcast.monitor.LocalTopicStats;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.topic.impl.TopicProxySupport;
import com.hazelcast.topic.impl.TopicService;

public class TopicProxy<E>
extends TopicProxySupport
implements ITopic<E> {
    public TopicProxy(String name, NodeEngine nodeEngine, TopicService service) {
        super(name, nodeEngine, service);
    }

    @Override
    public void publish(E message) {
        this.publishInternal(message);
    }

    @Override
    public String addMessageListener(MessageListener<E> listener) {
        if (listener == null) {
            throw new NullPointerException("listener can't be null");
        }
        return this.addMessageListenerInternal(listener);
    }

    @Override
    public boolean removeMessageListener(String registrationId) {
        return this.removeMessageListenerInternal(registrationId);
    }

    @Override
    public LocalTopicStats getLocalTopicStats() {
        return this.getLocalTopicStatsInternal();
    }
}

