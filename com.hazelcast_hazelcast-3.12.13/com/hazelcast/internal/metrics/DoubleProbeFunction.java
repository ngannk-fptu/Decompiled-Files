/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics;

import com.hazelcast.internal.metrics.ProbeFunction;

public interface DoubleProbeFunction<S>
extends ProbeFunction {
    public double get(S var1) throws Exception;
}

