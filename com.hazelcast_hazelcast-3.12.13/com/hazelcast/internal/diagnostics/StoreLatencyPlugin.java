/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConcurrentReferenceHashMap;
import com.hazelcast.util.ConstructorFunction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class StoreLatencyPlugin
extends DiagnosticsPlugin {
    public static final HazelcastProperty PERIOD_SECONDS = new HazelcastProperty("hazelcast.diagnostics.storeLatency.period.seconds", 0, TimeUnit.SECONDS);
    public static final HazelcastProperty RESET_PERIOD_SECONDS = new HazelcastProperty("hazelcast.diagnostics.storeLatency.reset.period.seconds", 0, TimeUnit.SECONDS);
    private static final int LOW_WATERMARK_MICROS = 100;
    private static final int LATENCY_BUCKET_COUNT = 32;
    private static final String[] LATENCY_KEYS = new String[32];
    private final ConcurrentMap<String, ServiceProbes> metricsPerServiceMap = new ConcurrentHashMap<String, ServiceProbes>();
    private final ConstructorFunction<String, ServiceProbes> metricsPerServiceConstructorFunction = new ConstructorFunction<String, ServiceProbes>(){

        @Override
        public ServiceProbes createNew(String serviceName) {
            return new ServiceProbes(serviceName);
        }
    };
    private final ConstructorFunction<String, InstanceProbes> instanceProbesConstructorFunction = new ConstructorFunction<String, InstanceProbes>(){

        @Override
        public InstanceProbes createNew(String dataStructureName) {
            return new InstanceProbes(dataStructureName);
        }
    };
    private final long periodMillis;
    private final long resetPeriodMillis;
    private final long resetFrequency;
    private long iteration;

    public StoreLatencyPlugin(NodeEngineImpl nodeEngine) {
        this(nodeEngine.getLogger(StoreLatencyPlugin.class), nodeEngine.getProperties());
    }

    public StoreLatencyPlugin(ILogger logger, HazelcastProperties properties) {
        super(logger);
        this.periodMillis = properties.getMillis(PERIOD_SECONDS);
        this.resetPeriodMillis = properties.getMillis(RESET_PERIOD_SECONDS);
        this.resetFrequency = this.periodMillis == 0L || this.resetPeriodMillis == 0L ? 0L : Math.max(1L, this.resetPeriodMillis / this.periodMillis);
    }

    @Override
    public long getPeriodMillis() {
        return this.periodMillis;
    }

    @Override
    public void onStart() {
        this.logger.info("Plugin:active: period-millis:" + this.periodMillis + " resetPeriod-millis:" + this.resetPeriodMillis);
    }

    @Override
    public void run(DiagnosticsLogWriter writer) {
        ++this.iteration;
        this.render(writer);
        this.resetStatisticsIfNeeded();
    }

    private void render(DiagnosticsLogWriter writer) {
        for (ServiceProbes serviceProbes : this.metricsPerServiceMap.values()) {
            serviceProbes.render(writer);
        }
    }

    private void resetStatisticsIfNeeded() {
        if (this.resetFrequency > 0L && this.iteration % this.resetFrequency == 0L) {
            for (ServiceProbes serviceProbes : this.metricsPerServiceMap.values()) {
                serviceProbes.resetStatistics();
            }
        }
    }

    public long count(String serviceName, String dataStructureName, String methodName) {
        return ((LatencyProbeImpl)this.newProbe((String)serviceName, (String)dataStructureName, (String)methodName)).stats.count;
    }

    public LatencyProbe newProbe(String serviceName, String dataStructureName, String methodName) {
        ServiceProbes serviceProbes = ConcurrencyUtil.getOrPutIfAbsent(this.metricsPerServiceMap, serviceName, this.metricsPerServiceConstructorFunction);
        return serviceProbes.newProbe(dataStructureName, methodName);
    }

    static {
        long maxDurationForBucket = 100L;
        long p = 0L;
        for (int k = 0; k < LATENCY_KEYS.length; ++k) {
            StoreLatencyPlugin.LATENCY_KEYS[k] = p + ".." + (maxDurationForBucket - 1L) + "us";
            p = maxDurationForBucket;
            maxDurationForBucket *= 2L;
        }
    }

    public static interface LatencyProbe {
        public void recordValue(long var1);
    }

    static final class Statistics {
        private static final AtomicLongFieldUpdater<Statistics> COUNT = AtomicLongFieldUpdater.newUpdater(Statistics.class, "count");
        private static final AtomicLongFieldUpdater<Statistics> TOTAL_MICROS = AtomicLongFieldUpdater.newUpdater(Statistics.class, "totalMicros");
        private static final AtomicLongFieldUpdater<Statistics> MAX_MICROS = AtomicLongFieldUpdater.newUpdater(Statistics.class, "maxMicros");
        volatile long count;
        volatile long maxMicros;
        volatile long totalMicros;
        private final AtomicLongArray latencyDistribution = new AtomicLongArray(32);

        Statistics() {
        }

        private void recordValue(long durationNanos) {
            long currentMax;
            long durationMicros = TimeUnit.NANOSECONDS.toMicros(durationNanos);
            COUNT.addAndGet(this, 1L);
            TOTAL_MICROS.addAndGet(this, durationMicros);
            while (durationMicros > (currentMax = this.maxMicros) && !MAX_MICROS.compareAndSet(this, currentMax, durationMicros)) {
            }
            int bucketIndex = 0;
            long maxDurationForBucket = 100L;
            for (int k = 0; k < this.latencyDistribution.length() - 1 && durationMicros >= maxDurationForBucket; maxDurationForBucket *= 2L, ++k) {
                ++bucketIndex;
            }
            this.latencyDistribution.incrementAndGet(bucketIndex);
        }
    }

    static final class LatencyProbeImpl
    implements LatencyProbe {
        volatile Statistics stats = new Statistics();
        private final InstanceProbes instanceProbes;
        private final String methodName;

        private LatencyProbeImpl(String methodName, InstanceProbes instanceProbes) {
            this.methodName = methodName;
            this.instanceProbes = instanceProbes;
        }

        @Override
        public void recordValue(long durationNanos) {
            this.stats.recordValue(durationNanos);
        }

        private void render(DiagnosticsLogWriter writer) {
            Statistics stats = this.stats;
            long invocations = stats.count;
            long totalMicros = stats.totalMicros;
            long avgMicros = invocations == 0L ? 0L : totalMicros / invocations;
            long maxMicros = stats.maxMicros;
            if (invocations == 0L) {
                return;
            }
            writer.startSection(this.methodName);
            writer.writeKeyValueEntry("count", invocations);
            writer.writeKeyValueEntry("totalTime(us)", totalMicros);
            writer.writeKeyValueEntry("avg(us)", avgMicros);
            writer.writeKeyValueEntry("max(us)", maxMicros);
            writer.startSection("latency-distribution");
            for (int k = 0; k < stats.latencyDistribution.length(); ++k) {
                long value = stats.latencyDistribution.get(k);
                if (value <= 0L) continue;
                writer.writeKeyValueEntry(LATENCY_KEYS[k], value);
            }
            writer.endSection();
            writer.endSection();
        }

        private void resetStatistics() {
            this.stats = new Statistics();
        }
    }

    private static final class InstanceProbes {
        private final ConcurrentMap<String, LatencyProbeImpl> probes = new ConcurrentHashMap<String, LatencyProbeImpl>();
        private final String dataStructureName;

        InstanceProbes(String dataStructureName) {
            this.dataStructureName = dataStructureName;
        }

        LatencyProbe newProbe(String methodName) {
            LatencyProbeImpl probe = (LatencyProbeImpl)this.probes.get(methodName);
            if (probe == null) {
                LatencyProbeImpl newProbe = new LatencyProbeImpl(methodName, this);
                LatencyProbeImpl found = this.probes.putIfAbsent(methodName, newProbe);
                probe = found == null ? newProbe : found;
            }
            return probe;
        }

        private void render(DiagnosticsLogWriter writer) {
            writer.startSection(this.dataStructureName);
            for (LatencyProbeImpl probe : this.probes.values()) {
                probe.render(writer);
            }
            writer.endSection();
        }

        private void resetStatistics() {
            for (LatencyProbeImpl probe : this.probes.values()) {
                probe.resetStatistics();
            }
        }
    }

    private final class ServiceProbes {
        private final String serviceName;
        private final ConcurrentReferenceHashMap<String, InstanceProbes> instanceProbesMap = new ConcurrentReferenceHashMap(ConcurrentReferenceHashMap.ReferenceType.STRONG, ConcurrentReferenceHashMap.ReferenceType.WEAK);

        private ServiceProbes(String serviceName) {
            this.serviceName = serviceName;
        }

        private LatencyProbe newProbe(String dataStructureName, String methodName) {
            InstanceProbes instanceProbes = ConcurrencyUtil.getOrPutIfAbsent(this.instanceProbesMap, dataStructureName, StoreLatencyPlugin.this.instanceProbesConstructorFunction);
            return instanceProbes.newProbe(methodName);
        }

        private void render(DiagnosticsLogWriter writer) {
            writer.startSection(this.serviceName);
            for (InstanceProbes instanceProbes : this.instanceProbesMap.values()) {
                instanceProbes.render(writer);
            }
            writer.endSection();
        }

        private void resetStatistics() {
            for (InstanceProbes instanceProbes : this.instanceProbesMap.values()) {
                instanceProbes.resetStatistics();
            }
        }
    }
}

