/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.metrics;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.metrics.SdkMetric;

@SdkPublicApi
public interface MetricRecord<T> {
    public SdkMetric<T> metric();

    public T value();
}

