/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.MetricLabel
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.core.metrics.CacheType;

public interface MetricsRecorder {
    public void record(String var1, CacheType var2, MetricLabel var3, long var4);
}

