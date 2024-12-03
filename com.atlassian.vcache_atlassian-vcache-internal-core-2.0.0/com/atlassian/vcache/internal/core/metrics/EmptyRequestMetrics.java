/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.LongMetric
 *  com.atlassian.vcache.internal.MetricLabel
 *  com.atlassian.vcache.internal.RequestMetrics
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.internal.LongMetric;
import com.atlassian.vcache.internal.MetricLabel;
import com.atlassian.vcache.internal.RequestMetrics;
import java.util.Collections;
import java.util.Map;

public class EmptyRequestMetrics
implements RequestMetrics {
    public Map<String, Map<MetricLabel, ? extends LongMetric>> allJvmCacheLongMetrics() {
        return Collections.emptyMap();
    }

    public Map<String, Map<MetricLabel, ? extends LongMetric>> allRequestCacheLongMetrics() {
        return Collections.emptyMap();
    }

    public Map<String, Map<MetricLabel, ? extends LongMetric>> allExternalCacheLongMetrics() {
        return Collections.emptyMap();
    }
}

