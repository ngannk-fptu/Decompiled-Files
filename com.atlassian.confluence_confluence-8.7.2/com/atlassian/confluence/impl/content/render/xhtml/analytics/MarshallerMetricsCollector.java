/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.content.render.xhtml.analytics;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface MarshallerMetricsCollector {
    public @NonNull Timer executionStart();

    public @NonNull Timer streamingStart();

    public @NonNull MarshallerMetricsCollector addCustomMetric(String var1, long var2);

    public void publish();

    public static interface Timer {
        public void stop();
    }
}

