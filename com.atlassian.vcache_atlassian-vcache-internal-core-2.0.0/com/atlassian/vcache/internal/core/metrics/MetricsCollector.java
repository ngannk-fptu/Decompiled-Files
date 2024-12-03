/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.RequestContext
 *  com.atlassian.vcache.internal.RequestMetrics
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.RequestMetrics;
import com.atlassian.vcache.internal.core.Instrumentor;
import com.atlassian.vcache.internal.core.metrics.MetricsRecorder;

public interface MetricsCollector
extends Instrumentor,
MetricsRecorder {
    public RequestMetrics obtainRequestMetrics(RequestContext var1);
}

