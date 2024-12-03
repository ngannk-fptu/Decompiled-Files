/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics;

import com.hazelcast.internal.metrics.Gauge;

public interface DoubleGauge
extends Gauge {
    public double read();
}

