/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.internal.logging.InternalLogger
 *  io.micrometer.common.util.internal.logging.InternalLoggerFactory
 *  io.micrometer.common.util.internal.logging.WarnThenDebugLogger
 *  org.apache.kafka.common.Metric
 *  org.apache.kafka.common.MetricName
 *  org.apache.kafka.common.metrics.KafkaMetric
 *  org.apache.kafka.common.metrics.Measurable
 */
package io.micrometer.core.instrument.binder.kafka;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.common.util.internal.logging.WarnThenDebugLogger;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.metrics.Measurable;

@NonNullApi
@NonNullFields
@Incubating(since="1.4.0")
class KafkaMetrics
implements MeterBinder,
AutoCloseable {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(KafkaMetrics.class);
    private static final WarnThenDebugLogger warnThenDebugLogger = new WarnThenDebugLogger(KafkaMetrics.class);
    static final String METRIC_NAME_PREFIX = "kafka.";
    static final String METRIC_GROUP_APP_INFO = "app-info";
    static final String METRIC_GROUP_METRICS_COUNT = "kafka-metrics-count";
    static final String VERSION_METRIC_NAME = "version";
    static final String START_TIME_METRIC_NAME = "start-time-ms";
    static final Duration DEFAULT_REFRESH_INTERVAL = Duration.ofSeconds(60L);
    static final String KAFKA_VERSION_TAG_NAME = "kafka.version";
    static final String DEFAULT_VALUE = "unknown";
    private static final Set<Class<?>> counterMeasurableClasses = new HashSet();
    private final Supplier<Map<MetricName, ? extends Metric>> metricsSupplier;
    private final AtomicReference<Map<MetricName, ? extends Metric>> metrics = new AtomicReference();
    private final Iterable<Tag> extraTags;
    private final Duration refreshInterval;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("micrometer-kafka-metrics"));
    @Nullable
    private Iterable<Tag> commonTags;
    private volatile Set<MetricName> currentMeters = new HashSet<MetricName>();
    private String kafkaVersion = "unknown";
    @Nullable
    private volatile MeterRegistry registry;
    private final Set<Meter.Id> registeredMeterIds = ConcurrentHashMap.newKeySet();

    KafkaMetrics(Supplier<Map<MetricName, ? extends Metric>> metricsSupplier) {
        this(metricsSupplier, Collections.emptyList());
    }

    KafkaMetrics(Supplier<Map<MetricName, ? extends Metric>> metricsSupplier, Iterable<Tag> extraTags) {
        this(metricsSupplier, extraTags, DEFAULT_REFRESH_INTERVAL);
    }

    KafkaMetrics(Supplier<Map<MetricName, ? extends Metric>> metricsSupplier, Iterable<Tag> extraTags, Duration refreshInterval) {
        this.metricsSupplier = metricsSupplier;
        this.extraTags = extraTags;
        this.refreshInterval = refreshInterval;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        this.registry = registry;
        this.commonTags = this.getCommonTags(registry);
        this.prepareToBindMetrics(registry);
        this.checkAndBindMetrics(registry);
        this.scheduler.scheduleAtFixedRate(() -> this.checkAndBindMetrics(registry), this.getRefreshIntervalInMillis(), this.getRefreshIntervalInMillis(), TimeUnit.MILLISECONDS);
    }

    private Iterable<Tag> getCommonTags(MeterRegistry registry) {
        Meter.Id dummyId = Meter.builder("delete.this", Meter.Type.OTHER, Collections.emptyList()).register(registry).getId();
        registry.remove(dummyId);
        return dummyId.getTags();
    }

    void prepareToBindMetrics(MeterRegistry registry) {
        this.metrics.set(this.metricsSupplier.get());
        Map<MetricName, ? extends Metric> metrics = this.metrics.get();
        Metric startTimeMetric = null;
        for (Map.Entry<MetricName, ? extends Metric> entry : metrics.entrySet()) {
            MetricName name = entry.getKey();
            if (!METRIC_GROUP_APP_INFO.equals(name.group())) continue;
            if (VERSION_METRIC_NAME.equals(name.name())) {
                this.kafkaVersion = (String)entry.getValue().metricValue();
                continue;
            }
            if (!START_TIME_METRIC_NAME.equals(name.name())) continue;
            startTimeMetric = entry.getValue();
        }
        if (startTimeMetric != null) {
            MetricName startTimeMetricName = startTimeMetric.metricName();
            this.bindMeter(registry, startTimeMetric, this.meterName(startTimeMetricName), this.meterTags(startTimeMetricName));
        }
    }

    private long getRefreshIntervalInMillis() {
        return this.refreshInterval.toMillis();
    }

    void checkAndBindMetrics(MeterRegistry registry) {
        try {
            Map<MetricName, ? extends Metric> currentMetrics = this.metricsSupplier.get();
            this.metrics.set(currentMetrics);
            if (!this.currentMeters.equals(currentMetrics.keySet())) {
                Set metricsToRemove = this.currentMeters.stream().filter(metricName -> !currentMetrics.containsKey(metricName)).collect(Collectors.toSet());
                for (MetricName metricName2 : metricsToRemove) {
                    Meter.Id id = this.meterIdForComparison(metricName2);
                    registry.remove(id);
                    this.registeredMeterIds.remove(id);
                }
                this.currentMeters = new HashSet<MetricName>(currentMetrics.keySet());
                Map<String, List<Meter>> registryMetersByNames = registry.getMeters().stream().collect(Collectors.groupingBy(meter -> meter.getId().getName()));
                currentMetrics.forEach((name, metric) -> {
                    if (!(metric.metricValue() instanceof Number) || METRIC_GROUP_APP_INFO.equals(name.group()) || METRIC_GROUP_METRICS_COUNT.equals(name.group())) {
                        return;
                    }
                    String meterName = this.meterName((MetricName)name);
                    boolean hasLessTags = false;
                    for (Meter other : registryMetersByNames.getOrDefault(meterName, Collections.emptyList())) {
                        Meter.Id otherId = other.getId();
                        List<Tag> tags = otherId.getTags();
                        List<Tag> meterTagsWithCommonTags = this.meterTags((MetricName)name, true);
                        if (tags.size() < meterTagsWithCommonTags.size()) {
                            registry.remove(otherId);
                            this.registeredMeterIds.remove(otherId);
                            continue;
                        }
                        if (tags.size() == meterTagsWithCommonTags.size()) {
                            if (!tags.containsAll(meterTagsWithCommonTags)) break;
                            return;
                        }
                        hasLessTags = true;
                    }
                    if (hasLessTags) {
                        return;
                    }
                    List<Tag> tags = this.meterTags((MetricName)name);
                    try {
                        Meter meter = this.bindMeter(registry, (Metric)metric, meterName, (Iterable<Tag>)tags);
                        List meters = registryMetersByNames.computeIfAbsent(meterName, k -> new ArrayList());
                        meters.add(meter);
                    }
                    catch (Exception ex) {
                        String message = ex.getMessage();
                        if (message != null && message.contains("Prometheus requires")) {
                            warnThenDebugLogger.log(() -> "Failed to bind meter: " + meterName + " " + tags + ". However, this could happen and might be restored in the next refresh.");
                        }
                        log.warn("Failed to bind meter: " + meterName + " " + tags + ".", (Throwable)ex);
                    }
                });
            }
        }
        catch (Exception e) {
            log.warn("Failed to bind KafkaMetric", (Throwable)e);
        }
    }

    private Meter bindMeter(MeterRegistry registry, Metric metric, String meterName, Iterable<Tag> tags) {
        Meter meter = this.registerMeter(registry, metric, meterName, tags);
        this.registeredMeterIds.add(meter.getId());
        return meter;
    }

    private Meter registerMeter(MeterRegistry registry, Metric metric, String meterName, Iterable<Tag> tags) {
        MetricName metricName = metric.metricName();
        Class<? extends Measurable> measurableClass = KafkaMetrics.getMeasurableClass(metric);
        if (measurableClass == null && meterName.endsWith("total") || measurableClass != null && counterMeasurableClasses.contains(measurableClass)) {
            return this.registerCounter(registry, metricName, meterName, tags);
        }
        return this.registerGauge(registry, metricName, meterName, tags);
    }

    @Nullable
    private static Class<? extends Measurable> getMeasurableClass(Metric metric) {
        if (!(metric instanceof KafkaMetric)) {
            return null;
        }
        try {
            return ((KafkaMetric)metric).measurable().getClass();
        }
        catch (IllegalStateException ex) {
            return null;
        }
    }

    private Gauge registerGauge(MeterRegistry registry, MetricName metricName, String meterName, Iterable<Tag> tags) {
        return Gauge.builder(meterName, this.metrics, this.toMetricValue(metricName)).tags(tags).description(metricName.description()).register(registry);
    }

    private FunctionCounter registerCounter(MeterRegistry registry, MetricName metricName, String meterName, Iterable<Tag> tags) {
        return FunctionCounter.builder(meterName, this.metrics, this.toMetricValue(metricName)).tags(tags).description(metricName.description()).register(registry);
    }

    private ToDoubleFunction<AtomicReference<Map<MetricName, ? extends Metric>>> toMetricValue(MetricName metricName) {
        return metricsReference -> this.toDouble((Metric)((Map)metricsReference.get()).get(metricName));
    }

    private double toDouble(@Nullable Metric metric) {
        return metric != null ? ((Number)metric.metricValue()).doubleValue() : Double.NaN;
    }

    private List<Tag> meterTags(MetricName metricName, boolean includeCommonTags) {
        ArrayList<Tag> tags = new ArrayList<Tag>();
        metricName.tags().forEach((key, value) -> tags.add(Tag.of(key.replaceAll("-", "."), value)));
        tags.add(Tag.of(KAFKA_VERSION_TAG_NAME, this.kafkaVersion));
        this.extraTags.forEach(tags::add);
        if (includeCommonTags) {
            this.commonTags.forEach(tags::add);
        }
        return tags;
    }

    private List<Tag> meterTags(MetricName metricName) {
        return this.meterTags(metricName, false);
    }

    private String meterName(MetricName metricName) {
        String name = METRIC_NAME_PREFIX + metricName.group() + "." + metricName.name();
        return name.replaceAll("-metrics", "").replaceAll("-", ".");
    }

    private Meter.Id meterIdForComparison(MetricName metricName) {
        return new Meter.Id(this.meterName(metricName), Tags.of(this.meterTags(metricName, true)), null, null, Meter.Type.OTHER);
    }

    @Override
    public void close() {
        this.scheduler.shutdownNow();
        for (Meter.Id id : this.registeredMeterIds) {
            this.registry.remove(id);
        }
    }

    static {
        HashSet<String> classNames = new HashSet<String>();
        classNames.add("org.apache.kafka.common.metrics.stats.CumulativeSum");
        classNames.add("org.apache.kafka.common.metrics.stats.CumulativeCount");
        for (String className : classNames) {
            try {
                counterMeasurableClasses.add(Class.forName(className));
            }
            catch (ClassNotFoundException classNotFoundException) {}
        }
    }
}

