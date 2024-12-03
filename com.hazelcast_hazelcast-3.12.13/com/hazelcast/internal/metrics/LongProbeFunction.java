/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics;

import com.hazelcast.internal.metrics.ProbeFunction;

public interface LongProbeFunction<S>
extends ProbeFunction {
    public long get(S var1) throws Exception;
}

