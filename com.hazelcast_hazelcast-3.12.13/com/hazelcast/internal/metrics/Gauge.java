/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics;

import com.hazelcast.internal.metrics.Metric;

public interface Gauge
extends Metric {
    public void render(StringBuilder var1);
}

