/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.instrumentation.AtomicCounter
 *  com.atlassian.instrumentation.Instrument
 *  com.atlassian.instrumentation.InstrumentRegistry
 *  com.atlassian.instrumentation.operations.OpCounter
 *  io.micrometer.core.instrument.Counter
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tag
 *  io.micrometer.core.instrument.Timer
 *  io.micrometer.core.instrument.Timer$Sample
 *  javax.annotation.PostConstruct
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.impl.metrics.ConfluenceMicrometer;
import com.atlassian.confluence.util.profiling.AtlassianInstrumentation;
import com.atlassian.confluence.util.profiling.AtlassianInstrumentationCounter;
import com.atlassian.confluence.util.profiling.AtlassianInstrumentationSplit;
import com.atlassian.confluence.util.profiling.AtlassianInstrumentationTimerSnapshot;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoringControl;
import com.atlassian.confluence.util.profiling.ControllableInstrumentRegistry;
import com.atlassian.confluence.util.profiling.Counter;
import com.atlassian.confluence.util.profiling.CounterSnapshot;
import com.atlassian.confluence.util.profiling.DefaultCounterSnapshot;
import com.atlassian.confluence.util.profiling.MutableRegistryConfiguration;
import com.atlassian.confluence.util.profiling.NopCounter;
import com.atlassian.confluence.util.profiling.NopSplit;
import com.atlassian.confluence.util.profiling.Split;
import com.atlassian.confluence.util.profiling.TimerSnapshot;
import com.atlassian.instrumentation.AtomicCounter;
import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.InstrumentRegistry;
import com.atlassian.instrumentation.operations.OpCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
@Internal
public class AtlassianInstrumentationConfluenceMonitoring
implements ConfluenceMonitoring,
ConfluenceMonitoringControl {
    private static final String OVERFLOW_COUNTER_NAME = "[COUNTER_OVERFLOW]";
    private static final String OVERFLOW_TIMER_NAME = "[TIMER_OVERFLOW]";
    private static final int DEFAULT_MAX_ENTRIES = 5000;
    private final int maxEntries;
    private final ControllableInstrumentRegistry registry;
    private final AtlassianInstrumentation.AtlasSplitFactory hibernateSplitFactory = new ConfluenceAtlasSplitFactory(this);
    private final MutableRegistryConfiguration registryConfiguration;
    private final MeterRegistry micrometerRegistry;

    public AtlassianInstrumentationConfluenceMonitoring(ControllableInstrumentRegistry instrumentRegistry, MutableRegistryConfiguration registryConfiguration, MeterRegistry micrometerRegistry) {
        this(5000, instrumentRegistry, registryConfiguration, micrometerRegistry);
    }

    @VisibleForTesting
    AtlassianInstrumentationConfluenceMonitoring(int maxEntries, ControllableInstrumentRegistry instrumentRegistry, MutableRegistryConfiguration registryConfiguration, MeterRegistry micrometerRegistry) {
        this.maxEntries = maxEntries;
        this.registry = instrumentRegistry;
        this.registryConfiguration = registryConfiguration;
        this.micrometerRegistry = micrometerRegistry;
    }

    public void disableAllMonitoring() throws Exception {
        this.disableMonitoring();
        this.disableCpuTiming();
        this.disableHibernateMonitoring();
    }

    @PostConstruct
    void initialiseMonitoring() {
        if (ConfluenceMicrometer.isMicrometerEnabled()) {
            this.enableMonitoring();
        } else {
            this.disableMonitoring();
        }
        this.disableCpuTiming();
        this.disableHibernateMonitoring();
    }

    @Override
    public @NonNull Counter fetchCounter(String name, String ... optional) {
        ControllableInstrumentRegistry current = this.registry;
        if (!this.isMonitoringEnabled()) {
            return NopCounter.INSTANCE;
        }
        if (ConfluenceMicrometer.isMicrometerEnabled()) {
            return this.getMicrometerCounter(this.createName(name, optional), Collections.emptySet());
        }
        return this.fetchAtlassianInstrumentationCounter(current, this.createName(name, optional));
    }

    @Override
    public @NonNull Counter fetchCounter(String name, Map<String, String> tags) {
        ControllableInstrumentRegistry current = this.registry;
        if (!this.isMonitoringEnabled()) {
            return NopCounter.INSTANCE;
        }
        if (ConfluenceMicrometer.isMicrometerEnabled()) {
            return this.getMicrometerCounter(name, AtlassianInstrumentationConfluenceMonitoring.micrometerTags(tags));
        }
        return this.fetchAtlassianInstrumentationCounter(current, this.createName(name, AtlassianInstrumentationConfluenceMonitoring.values(tags)));
    }

    @Override
    public @NonNull Split startSplit(String name, String ... optional) {
        ControllableInstrumentRegistry current = this.registry;
        if (!this.isMonitoringEnabled()) {
            return NopSplit.INSTANCE;
        }
        if (ConfluenceMicrometer.isMicrometerEnabled()) {
            return this.startMicrometerSplit(this.createName(name, optional), Collections.emptySet());
        }
        return this.startAtlassianInstrumentationSplit(current, this.createName(name, optional));
    }

    @Override
    public @NonNull Split startSplit(String name, Map<String, String> tags) {
        ControllableInstrumentRegistry current = this.registry;
        if (!this.isMonitoringEnabled()) {
            return NopSplit.INSTANCE;
        }
        if (ConfluenceMicrometer.isMicrometerEnabled()) {
            return this.startMicrometerSplit(name, AtlassianInstrumentationConfluenceMonitoring.micrometerTags(tags));
        }
        return this.startAtlassianInstrumentationSplit(current, this.createName(name, AtlassianInstrumentationConfluenceMonitoring.values(tags)));
    }

    private AtlassianInstrumentationCounter fetchAtlassianInstrumentationCounter(InstrumentRegistry registry, String name) {
        String guardedName = this.guardNameForOverflow(registry, name, OVERFLOW_COUNTER_NAME);
        return new AtlassianInstrumentationCounter(registry.pullCounter(guardedName));
    }

    private AtlassianInstrumentationSplit startAtlassianInstrumentationSplit(InstrumentRegistry registry, String name) {
        String guardedName = this.guardNameForOverflow(registry, name, OVERFLOW_TIMER_NAME);
        return new AtlassianInstrumentationSplit(registry.pullTimer(guardedName));
    }

    @Override
    public boolean isMonitoringEnabled() {
        return this.registry.isMonitoringEnabled();
    }

    @Override
    public void enableMonitoring() {
        this.registry.enableMonitoring();
    }

    @Override
    public void disableMonitoring() {
        this.registry.disableMonitoring();
    }

    @Override
    public boolean isCpuTimingEnabled() {
        return this.registryConfiguration.isCPUCostCollected();
    }

    @Override
    public void enableCpuTiming() {
        this.registryConfiguration.setCpuCostCollected(true);
    }

    @Override
    public void disableCpuTiming() {
        this.registryConfiguration.setCpuCostCollected(false);
    }

    @Override
    public void enableHibernateMonitoring() {
        AtlassianInstrumentation.registerFactory(this.hibernateSplitFactory);
    }

    @Override
    public void disableHibernateMonitoring() {
        AtlassianInstrumentation.unregisterFactory(this.hibernateSplitFactory);
    }

    @Override
    public void clear() {
        this.registry.clear();
    }

    @Override
    public @NonNull List<CounterSnapshot> snapshotCounters() {
        ControllableInstrumentRegistry current = this.registry;
        if (current == null) {
            return new ArrayList<CounterSnapshot>();
        }
        return current.snapshotInstruments().stream().filter(instrument -> instrument instanceof AtomicCounter).map(instrument -> (AtomicCounter)instrument).map(ac -> new DefaultCounterSnapshot(ac.getName(), ac.getValue())).collect(Collectors.toList());
    }

    @Override
    public @NonNull List<TimerSnapshot> snapshotTimers() {
        ControllableInstrumentRegistry current = this.registry;
        if (current == null) {
            return new ArrayList<TimerSnapshot>();
        }
        return current.snapshotInstruments().stream().filter(instrument -> instrument instanceof OpCounter).map(instrument -> (OpCounter)instrument).map(oc -> new AtlassianInstrumentationTimerSnapshot(oc.snapshot())).collect(Collectors.toList());
    }

    private String guardNameForOverflow(InstrumentRegistry current, String fullName, String overflowName) {
        Instrument instrument = current.getInstrument(fullName);
        return instrument != null || current.getNumberOfInstruments() < this.maxEntries ? fullName : overflowName;
    }

    @VisibleForTesting
    InstrumentRegistry getRegistry() {
        return this.registry;
    }

    private Counter getMicrometerCounter(String name, Collection<Tag> tags) {
        final io.micrometer.core.instrument.Counter counter = this.micrometerRegistry.counter(AtlassianInstrumentationConfluenceMonitoring.getMicrometerMeterName(name), tags);
        return new Counter(){

            @Override
            public Counter increase() {
                counter.increment();
                return this;
            }

            @Override
            public Counter increase(long amount) {
                counter.increment((double)amount);
                return this;
            }
        };
    }

    private Split startMicrometerSplit(String name, Collection<Tag> tags) {
        final Timer timer = this.micrometerRegistry.timer(AtlassianInstrumentationConfluenceMonitoring.getMicrometerMeterName(name), tags);
        final Timer.Sample sample = Timer.start((MeterRegistry)this.micrometerRegistry);
        return new Split(){

            @Override
            public Split stop() {
                sample.stop(timer);
                return this;
            }
        };
    }

    private static String getMicrometerMeterName(String name) {
        return ConfluenceMonitoring.class.getSimpleName() + "." + name;
    }

    private static Collection<Tag> micrometerTags(Map<String, String> tags) {
        if (tags.isEmpty()) {
            return Collections.emptySet();
        }
        return tags.entrySet().stream().map(entry -> Tag.of((String)((String)entry.getKey()), (String)((String)entry.getValue()))).collect(Collectors.toSet());
    }

    private static String[] values(Map<String, String> tags) {
        if (tags.isEmpty()) {
            return new String[0];
        }
        return tags.values().toArray(new String[0]);
    }

    private static class ConfluenceAtlasSplitFactory
    implements AtlassianInstrumentation.AtlasSplitFactory {
        private final ConfluenceMonitoring monitoring;

        ConfluenceAtlasSplitFactory(ConfluenceMonitoring monitoring) {
            this.monitoring = monitoring;
        }

        @Override
        public AtlassianInstrumentation.AtlasSplit startSplit(String name) {
            return new ConfluenceAtlasSplit(this.monitoring.startSplit(name));
        }
    }

    private static class ConfluenceAtlasSplit
    implements AtlassianInstrumentation.AtlasSplit {
        private final Split split;

        ConfluenceAtlasSplit(Split split) {
            this.split = split;
        }

        @Override
        public void stop() {
            this.split.stop();
        }
    }
}

