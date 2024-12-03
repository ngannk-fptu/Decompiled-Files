/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.ToString
 */
package software.amazon.awssdk.metrics.internal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.metrics.MetricCollection;
import software.amazon.awssdk.metrics.MetricRecord;
import software.amazon.awssdk.metrics.SdkMetric;
import software.amazon.awssdk.utils.ToString;

@SdkInternalApi
public final class DefaultMetricCollection
implements MetricCollection {
    private final String name;
    private final Map<SdkMetric<?>, List<MetricRecord<?>>> metrics;
    private final List<MetricCollection> children;
    private final Instant creationTime;

    public DefaultMetricCollection(String name, Map<SdkMetric<?>, List<MetricRecord<?>>> metrics, List<MetricCollection> children) {
        this.name = name;
        this.metrics = new HashMap(metrics);
        this.children = children != null ? new ArrayList<MetricCollection>(children) : Collections.emptyList();
        this.creationTime = Instant.now();
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public <T> List<T> metricValues(SdkMetric<T> metric) {
        if (this.metrics.containsKey(metric)) {
            List<MetricRecord<?>> metricRecords = this.metrics.get(metric);
            List values = metricRecords.stream().map(MetricRecord::value).collect(Collectors.toList());
            return Collections.unmodifiableList(values);
        }
        return Collections.emptyList();
    }

    @Override
    public List<MetricCollection> children() {
        return Collections.unmodifiableList(this.children);
    }

    @Override
    public Instant creationTime() {
        return this.creationTime;
    }

    @Override
    public Iterator<MetricRecord<?>> iterator() {
        return this.metrics.values().stream().flatMap(Collection::stream).iterator();
    }

    public String toString() {
        return ToString.builder((String)"MetricCollection").add("name", (Object)this.name).add("metrics", this.metrics.values().stream().flatMap(Collection::stream).collect(Collectors.toList())).add("children", this.children).build();
    }
}

