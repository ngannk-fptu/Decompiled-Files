/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.metrics;

import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.metrics.MetricCollection;
import software.amazon.awssdk.metrics.SdkMetric;
import software.amazon.awssdk.metrics.internal.DefaultMetricCollector;

@NotThreadSafe
@SdkPublicApi
public interface MetricCollector {
    public String name();

    public <T> void reportMetric(SdkMetric<T> var1, T var2);

    public MetricCollector createChild(String var1);

    public MetricCollection collect();

    public static MetricCollector create(String name) {
        return DefaultMetricCollector.create(name);
    }
}

