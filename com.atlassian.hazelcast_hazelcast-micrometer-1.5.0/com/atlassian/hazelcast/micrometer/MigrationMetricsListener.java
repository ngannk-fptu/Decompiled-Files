/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.MigrationEvent
 *  com.hazelcast.core.MigrationListener
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tag
 */
package com.atlassian.hazelcast.micrometer;

import com.hazelcast.core.MigrationEvent;
import com.hazelcast.core.MigrationListener;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.Arrays;
import java.util.List;

final class MigrationMetricsListener
implements MigrationListener {
    private static final String METER_PREFIX = "hazelcast.migration.";
    private final MeterRegistry meterRegistry;

    MigrationMetricsListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void migrationStarted(MigrationEvent event) {
        this.incrementCounter("started", this.tags(event));
    }

    public void migrationCompleted(MigrationEvent event) {
        this.incrementCounter("completed", this.tags(event));
    }

    public void migrationFailed(MigrationEvent event) {
        this.incrementCounter("failed", this.tags(event));
    }

    private void incrementCounter(String meterName, Iterable<Tag> tags) {
        this.meterRegistry.counter(METER_PREFIX + meterName, tags).increment();
    }

    private List<Tag> tags(MigrationEvent event) {
        return Arrays.asList(Tag.of((String)"partitionId", (String)String.valueOf(event.getPartitionId())));
    }
}

