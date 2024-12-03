/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.metrics.RequestMetricType;

public enum AwsClientSideMonitoringMetrics implements RequestMetricType
{
    ApiCallLatency,
    MaxRetriesExceeded;

}

