/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.metrics.internal;

import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.metrics.MetricCollection;
import software.amazon.awssdk.metrics.MetricRecord;
import software.amazon.awssdk.metrics.SdkMetric;

@SdkInternalApi
public final class EmptyMetricCollection
implements MetricCollection {
    private final Instant creationTime = Instant.now();

    @Override
    public String name() {
        return "NoOp";
    }

    @Override
    public <T> List<T> metricValues(SdkMetric<T> metric) {
        return Collections.emptyList();
    }

    @Override
    public List<MetricCollection> children() {
        return Collections.emptyList();
    }

    @Override
    public Instant creationTime() {
        return this.creationTime;
    }

    @Override
    public Iterator<MetricRecord<?>> iterator() {
        return Collections.emptyIterator();
    }

    public static EmptyMetricCollection create() {
        return new EmptyMetricCollection();
    }
}

