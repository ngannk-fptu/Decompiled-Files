/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics;

import com.hazelcast.internal.metrics.MetricsProvider;
import com.hazelcast.internal.metrics.MetricsRegistry;

public interface DiscardableMetricsProvider
extends MetricsProvider {
    public void discardMetrics(MetricsRegistry var1);
}

