/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.metrics.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.metrics.MetricCollection;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.metrics.MetricRecord;
import software.amazon.awssdk.metrics.SdkMetric;
import software.amazon.awssdk.metrics.internal.DefaultMetricCollection;
import software.amazon.awssdk.metrics.internal.DefaultMetricRecord;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultMetricCollector
implements MetricCollector {
    private static final Logger log = Logger.loggerFor(DefaultMetricCollector.class);
    private final String name;
    private final Map<SdkMetric<?>, List<MetricRecord<?>>> metrics = new LinkedHashMap();
    private final List<MetricCollector> children = new ArrayList<MetricCollector>();

    public DefaultMetricCollector(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public synchronized <T> void reportMetric(SdkMetric<T> metric, T data) {
        this.metrics.computeIfAbsent(metric, m -> new ArrayList()).add(new DefaultMetricRecord<T>(metric, data));
    }

    @Override
    public synchronized MetricCollector createChild(String name) {
        DefaultMetricCollector child = new DefaultMetricCollector(name);
        this.children.add(child);
        return child;
    }

    @Override
    public synchronized MetricCollection collect() {
        List<MetricCollection> collectedChildren = this.children.stream().map(MetricCollector::collect).collect(Collectors.toList());
        DefaultMetricCollection metricRecords = new DefaultMetricCollection(this.name, this.metrics, collectedChildren);
        log.debug(() -> "Collected metrics records: " + metricRecords);
        return metricRecords;
    }

    public static MetricCollector create(String name) {
        Validate.notEmpty((CharSequence)name, (String)"name", (Object[])new Object[0]);
        return new DefaultMetricCollector(name);
    }

    public String toString() {
        return ToString.builder((String)"DefaultMetricCollector").add("metrics", this.metrics).build();
    }
}

