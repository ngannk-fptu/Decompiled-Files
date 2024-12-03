/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.metrics;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.metrics.MetricRecord;
import software.amazon.awssdk.metrics.SdkMetric;

@SdkPublicApi
public interface MetricCollection
extends Iterable<MetricRecord<?>> {
    public String name();

    default public Stream<MetricRecord<?>> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public <T> List<T> metricValues(SdkMetric<T> var1);

    public List<MetricCollection> children();

    default public Stream<MetricCollection> childrenWithName(String name) {
        return this.children().stream().filter(c -> c.name().equals(name));
    }

    public Instant creationTime();
}

