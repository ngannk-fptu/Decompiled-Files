/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.Message
 *  com.hazelcast.core.MessageListener
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tag
 */
package com.atlassian.hazelcast.micrometer;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.Arrays;
import java.util.Collection;

final class MessageMetricsListener<T>
implements MessageListener<T> {
    private static final String METER_PREFIX = "hazelcast.topic.";
    private final MeterRegistry meterRegistry;

    MessageMetricsListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void onMessage(Message<T> event) {
        this.incrementCounter(event, "messageReceived");
    }

    private void incrementCounter(Message<T> event, String name) {
        this.meterRegistry.counter(METER_PREFIX + name, this.tags(event)).increment();
    }

    private Collection<Tag> tags(Message<T> event) {
        return Arrays.asList(Tag.of((String)"source", (String)String.valueOf(event.getSource())), Tag.of((String)"publishingMember", (String)String.valueOf(event.getPublishingMember().getAddress())));
    }
}

