/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.MessageListener;
import com.hazelcast.monitor.LocalTopicStats;

public interface ITopic<E>
extends DistributedObject {
    @Override
    public String getName();

    public void publish(E var1);

    public String addMessageListener(MessageListener<E> var1);

    public boolean removeMessageListener(String var1);

    public LocalTopicStats getLocalTopicStats();
}

