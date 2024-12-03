/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.metrics.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.metrics.MetricRecord;
import software.amazon.awssdk.metrics.SdkMetric;
import software.amazon.awssdk.utils.ToString;

@SdkInternalApi
public final class DefaultMetricRecord<T>
implements MetricRecord<T> {
    private final SdkMetric<T> metric;
    private final T value;

    public DefaultMetricRecord(SdkMetric<T> metric, T value) {
        this.metric = metric;
        this.value = value;
    }

    @Override
    public SdkMetric<T> metric() {
        return this.metric;
    }

    @Override
    public T value() {
        return this.value;
    }

    public String toString() {
        return ToString.builder("MetricRecord").add("metric", this.metric.name()).add("value", this.value).build();
    }
}

