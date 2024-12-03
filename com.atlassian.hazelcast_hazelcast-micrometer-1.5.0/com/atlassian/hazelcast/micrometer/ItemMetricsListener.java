/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.ItemEvent
 *  com.hazelcast.core.ItemListener
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tag
 */
package com.atlassian.hazelcast.micrometer;

import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.Collection;
import java.util.Collections;

final class ItemMetricsListener<T>
implements ItemListener<T> {
    private static final String METER_PREFIX = "hazelcast.collection.";
    private final MeterRegistry meterRegistry;

    ItemMetricsListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void itemAdded(ItemEvent<T> event) {
        this.incrementCounter(event, "itemAdded");
    }

    public void itemRemoved(ItemEvent<T> event) {
        this.incrementCounter(event, "itemRemoved");
    }

    private void incrementCounter(ItemEvent<T> event, String name) {
        this.meterRegistry.counter(METER_PREFIX + name, this.tags(event)).increment();
    }

    private Collection<Tag> tags(ItemEvent<T> event) {
        return Collections.singletonList(Tag.of((String)"source", (String)String.valueOf(event.getSource())));
    }
}

