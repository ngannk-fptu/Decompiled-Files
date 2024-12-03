/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal;

import com.atlassian.vcache.internal.LongMetric;
import com.atlassian.vcache.internal.MetricLabel;
import java.util.Map;

public interface RequestMetrics {
    public Map<String, Map<MetricLabel, ? extends LongMetric>> allJvmCacheLongMetrics();

    public Map<String, Map<MetricLabel, ? extends LongMetric>> allRequestCacheLongMetrics();

    public Map<String, Map<MetricLabel, ? extends LongMetric>> allExternalCacheLongMetrics();
}

