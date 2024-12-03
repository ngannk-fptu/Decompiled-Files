/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Counting;
import com.codahale.metrics.Metric;

public interface Metered
extends Metric,
Counting {
    @Override
    public long getCount();

    public double getFifteenMinuteRate();

    public double getFiveMinuteRate();

    public double getMeanRate();

    public double getOneMinuteRate();
}

