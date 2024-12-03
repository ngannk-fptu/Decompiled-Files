/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.metrics;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.metrics.MetricCollection;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.metrics.SdkMetric;
import software.amazon.awssdk.metrics.internal.EmptyMetricCollection;

@SdkPublicApi
public final class NoOpMetricCollector
implements MetricCollector {
    private static final NoOpMetricCollector INSTANCE = new NoOpMetricCollector();

    private NoOpMetricCollector() {
    }

    @Override
    public String name() {
        return "NoOp";
    }

    @Override
    public <T> void reportMetric(SdkMetric<T> metric, T data) {
    }

    @Override
    public MetricCollector createChild(String name) {
        return INSTANCE;
    }

    @Override
    public MetricCollection collect() {
        return EmptyMetricCollection.create();
    }

    public static NoOpMetricCollector create() {
        return INSTANCE;
    }
}

