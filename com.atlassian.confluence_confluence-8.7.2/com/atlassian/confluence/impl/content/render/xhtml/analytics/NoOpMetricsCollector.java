/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.content.render.xhtml.analytics;

import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollector;
import org.checkerframework.checker.nullness.qual.NonNull;

class NoOpMetricsCollector
implements MarshallerMetricsCollector {
    static final MarshallerMetricsCollector INSTANCE = new NoOpMetricsCollector();
    private final MarshallerMetricsCollector.Timer TIMER = () -> {};

    private NoOpMetricsCollector() {
    }

    @Override
    public @NonNull MarshallerMetricsCollector.Timer executionStart() {
        return this.TIMER;
    }

    @Override
    public @NonNull MarshallerMetricsCollector.Timer streamingStart() {
        return this.TIMER;
    }

    @Override
    public @NonNull MarshallerMetricsCollector addCustomMetric(String name, long value) {
        return this;
    }

    @Override
    public void publish() {
    }
}

