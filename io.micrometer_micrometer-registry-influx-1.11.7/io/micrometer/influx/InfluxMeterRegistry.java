/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.util.StringUtils
 *  io.micrometer.core.instrument.Clock
 *  io.micrometer.core.instrument.DistributionSummary
 *  io.micrometer.core.instrument.FunctionTimer
 *  io.micrometer.core.instrument.LongTaskTimer
 *  io.micrometer.core.instrument.Measurement
 *  io.micrometer.core.instrument.Meter
 *  io.micrometer.core.instrument.Meter$Id
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Timer
 *  io.micrometer.core.instrument.config.NamingConvention
 *  io.micrometer.core.instrument.step.StepMeterRegistry
 *  io.micrometer.core.instrument.step.StepRegistryConfig
 *  io.micrometer.core.instrument.util.DoubleFormat
 *  io.micrometer.core.instrument.util.MeterPartition
 *  io.micrometer.core.instrument.util.NamedThreadFactory
 *  io.micrometer.core.ipc.http.HttpSender
 *  io.micrometer.core.ipc.http.HttpSender$Request$Builder
 *  io.micrometer.core.ipc.http.HttpUrlConnectionSender
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.micrometer.influx;

import io.micrometer.common.util.StringUtils;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import io.micrometer.core.instrument.util.DoubleFormat;
import io.micrometer.core.instrument.util.MeterPartition;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import io.micrometer.core.ipc.http.HttpSender;
import io.micrometer.core.ipc.http.HttpUrlConnectionSender;
import io.micrometer.influx.CreateDatabaseQueryBuilder;
import io.micrometer.influx.InfluxApiVersion;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxNamingConvention;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfluxMeterRegistry
extends StepMeterRegistry {
    private static final ThreadFactory DEFAULT_THREAD_FACTORY = new NamedThreadFactory("influx-metrics-publisher");
    private final InfluxConfig config;
    private final HttpSender httpClient;
    private final Logger logger = LoggerFactory.getLogger(InfluxMeterRegistry.class);
    private boolean databaseExists = false;

    public InfluxMeterRegistry(InfluxConfig config, Clock clock) {
        this(config, clock, DEFAULT_THREAD_FACTORY, (HttpSender)new HttpUrlConnectionSender(config.connectTimeout(), config.readTimeout()));
    }

    @Deprecated
    public InfluxMeterRegistry(InfluxConfig config, Clock clock, ThreadFactory threadFactory) {
        this(config, clock, threadFactory, (HttpSender)new HttpUrlConnectionSender(config.connectTimeout(), config.readTimeout()));
    }

    private InfluxMeterRegistry(InfluxConfig config, Clock clock, ThreadFactory threadFactory, HttpSender httpClient) {
        super((StepRegistryConfig)config, clock);
        this.config().namingConvention((NamingConvention)new InfluxNamingConvention());
        this.config = config;
        this.httpClient = httpClient;
        this.start(threadFactory);
    }

    public void start(ThreadFactory threadFactory) {
        super.start(threadFactory);
        if (this.config.enabled()) {
            this.logger.info("Using InfluxDB API version {} to write metrics", (Object)this.config.apiVersion());
        }
    }

    public static Builder builder(InfluxConfig config) {
        return new Builder(config);
    }

    private void createDatabaseIfNecessary() {
        if (!this.config.autoCreateDb() || this.databaseExists || this.config.apiVersion() == InfluxApiVersion.V2) {
            return;
        }
        try {
            String createDatabaseQuery = new CreateDatabaseQueryBuilder(this.config.db()).setRetentionDuration(this.config.retentionDuration()).setRetentionPolicyName(this.config.retentionPolicy()).setRetentionReplicationFactor(this.config.retentionReplicationFactor()).setRetentionShardDuration(this.config.retentionShardDuration()).build();
            HttpSender.Request.Builder requestBuilder = this.httpClient.post(this.config.uri() + "/query?q=" + URLEncoder.encode(createDatabaseQuery, "UTF-8")).withBasicAuthentication(this.config.userName(), this.config.password());
            this.config.apiVersion().addHeaderToken(this.config, requestBuilder);
            requestBuilder.send().onSuccess(response -> {
                this.logger.debug("influx database {} is ready to receive metrics", (Object)this.config.db());
                this.databaseExists = true;
            }).onError(response -> this.logger.error("unable to create database '{}': {}", (Object)this.config.db(), (Object)response.body()));
        }
        catch (Throwable e) {
            this.logger.error("unable to create database '{}'", (Object)this.config.db(), (Object)e);
        }
    }

    protected void publish() {
        this.createDatabaseIfNecessary();
        try {
            String influxEndpoint = this.config.apiVersion().writeEndpoint(this.config);
            for (List batch : MeterPartition.partition((MeterRegistry)this, (int)this.config.batchSize())) {
                HttpSender.Request.Builder requestBuilder = this.httpClient.post(influxEndpoint).withBasicAuthentication(this.config.userName(), this.config.password());
                this.config.apiVersion().addHeaderToken(this.config, requestBuilder);
                requestBuilder.withPlainText(batch.stream().flatMap(m -> (Stream)m.match(gauge -> this.writeGauge(gauge.getId(), gauge.value()), counter -> this.writeCounter(counter.getId(), counter.count()), this::writeTimer, this::writeSummary, this::writeLongTaskTimer, gauge -> this.writeGauge(gauge.getId(), gauge.value(this.getBaseTimeUnit())), counter -> this.writeCounter(counter.getId(), counter.count()), this::writeFunctionTimer, this::writeMeter)).collect(Collectors.joining("\n"))).compressWhen(this.config::compressed).send().onSuccess(response -> {
                    this.logger.debug("successfully sent {} metrics to InfluxDB.", (Object)batch.size());
                    this.databaseExists = true;
                }).onError(response -> this.logger.error("failed to send metrics to influx: {}", (Object)response.body()));
            }
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed InfluxDB publishing endpoint, see '" + this.config.prefix() + ".uri'", e);
        }
        catch (Throwable e) {
            this.logger.error("failed to send metrics to influx", e);
        }
    }

    Stream<String> writeMeter(Meter m) {
        ArrayList<Field> fields = new ArrayList<Field>();
        for (Measurement measurement : m.measure()) {
            double value = measurement.getValue();
            if (!Double.isFinite(value)) continue;
            String fieldKey = measurement.getStatistic().getTagValueRepresentation().replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
            fields.add(new Field(fieldKey, value));
        }
        if (fields.isEmpty()) {
            return Stream.empty();
        }
        Meter.Id id = m.getId();
        return Stream.of(this.influxLineProtocol(id, id.getType().name().toLowerCase(), fields.stream()));
    }

    private Stream<String> writeLongTaskTimer(LongTaskTimer timer) {
        Stream<Field> fields = Stream.of(new Field("active_tasks", timer.activeTasks()), new Field("duration", timer.duration(this.getBaseTimeUnit())));
        return Stream.of(this.influxLineProtocol(timer.getId(), "long_task_timer", fields));
    }

    Stream<String> writeCounter(Meter.Id id, double count) {
        if (Double.isFinite(count)) {
            return Stream.of(this.influxLineProtocol(id, "counter", Stream.of(new Field("value", count))));
        }
        return Stream.empty();
    }

    Stream<String> writeGauge(Meter.Id id, Double value) {
        if (Double.isFinite(value)) {
            return Stream.of(this.influxLineProtocol(id, "gauge", Stream.of(new Field("value", value))));
        }
        return Stream.empty();
    }

    Stream<String> writeFunctionTimer(FunctionTimer timer) {
        double sum = timer.totalTime(this.getBaseTimeUnit());
        if (Double.isFinite(sum)) {
            Stream.Builder<Field> builder = Stream.builder();
            builder.add(new Field("sum", sum));
            builder.add(new Field("count", timer.count()));
            double mean = timer.mean(this.getBaseTimeUnit());
            if (Double.isFinite(mean)) {
                builder.add(new Field("mean", mean));
            }
            return Stream.of(this.influxLineProtocol(timer.getId(), "histogram", builder.build()));
        }
        return Stream.empty();
    }

    private Stream<String> writeTimer(Timer timer) {
        Stream<Field> fields = Stream.of(new Field("sum", timer.totalTime(this.getBaseTimeUnit())), new Field("count", timer.count()), new Field("mean", timer.mean(this.getBaseTimeUnit())), new Field("upper", timer.max(this.getBaseTimeUnit())));
        return Stream.of(this.influxLineProtocol(timer.getId(), "histogram", fields));
    }

    private Stream<String> writeSummary(DistributionSummary summary) {
        Stream<Field> fields = Stream.of(new Field("sum", summary.totalAmount()), new Field("count", summary.count()), new Field("mean", summary.mean()), new Field("upper", summary.max()));
        return Stream.of(this.influxLineProtocol(summary.getId(), "histogram", fields));
    }

    private String influxLineProtocol(Meter.Id id, String metricType, Stream<Field> fields) {
        String tags = this.getConventionTags(id).stream().filter(t -> StringUtils.isNotBlank((String)t.getValue())).map(t -> "," + t.getKey() + "=" + t.getValue()).collect(Collectors.joining(""));
        return this.getConventionName(id) + tags + ",metric_type=" + metricType + " " + fields.map(Field::toString).collect(Collectors.joining(",")) + " " + this.clock.wallTime();
    }

    protected final TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    static /* synthetic */ ThreadFactory access$000() {
        return DEFAULT_THREAD_FACTORY;
    }

    public static class Builder {
        private final InfluxConfig config;
        private Clock clock = Clock.SYSTEM;
        private ThreadFactory threadFactory = InfluxMeterRegistry.access$000();
        private HttpSender httpClient;

        Builder(InfluxConfig config) {
            this.config = config;
            this.httpClient = new HttpUrlConnectionSender(config.connectTimeout(), config.readTimeout());
        }

        public Builder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public Builder httpClient(HttpSender httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public InfluxMeterRegistry build() {
            return new InfluxMeterRegistry(this.config, this.clock, this.threadFactory, this.httpClient);
        }
    }

    static class Field {
        final String key;
        final double value;

        Field(String key, double value) {
            if (key.equals("time")) {
                throw new IllegalArgumentException("'time' is an invalid field key in InfluxDB");
            }
            this.key = key;
            this.value = value;
        }

        public String toString() {
            return this.key + "=" + DoubleFormat.decimalOrNan((double)this.value);
        }
    }
}

