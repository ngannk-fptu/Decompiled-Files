/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.composite;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeCounter;
import io.micrometer.core.instrument.composite.CompositeCustomMeter;
import io.micrometer.core.instrument.composite.CompositeDistributionSummary;
import io.micrometer.core.instrument.composite.CompositeFunctionCounter;
import io.micrometer.core.instrument.composite.CompositeFunctionTimer;
import io.micrometer.core.instrument.composite.CompositeGauge;
import io.micrometer.core.instrument.composite.CompositeLongTaskTimer;
import io.micrometer.core.instrument.composite.CompositeMeter;
import io.micrometer.core.instrument.composite.CompositeTimeGauge;
import io.micrometer.core.instrument.composite.CompositeTimer;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

public class CompositeMeterRegistry
extends MeterRegistry {
    private final AtomicBoolean registriesLock = new AtomicBoolean();
    private final Set<MeterRegistry> registries = Collections.newSetFromMap(new IdentityHashMap());
    private final Set<MeterRegistry> unmodifiableRegistries = Collections.unmodifiableSet(this.registries);
    volatile Set<MeterRegistry> nonCompositeDescendants = Collections.emptySet();
    private final AtomicBoolean parentLock = new AtomicBoolean();
    private volatile Set<CompositeMeterRegistry> parents = Collections.newSetFromMap(new IdentityHashMap());

    public CompositeMeterRegistry() {
        this(Clock.SYSTEM);
    }

    public CompositeMeterRegistry(Clock clock) {
        this(clock, Collections.emptySet());
    }

    public CompositeMeterRegistry(Clock clock, Iterable<MeterRegistry> registries) {
        super(clock);
        this.config().namingConvention(NamingConvention.identity).onMeterAdded(m -> {
            if (m instanceof CompositeMeter) {
                this.lock(this.registriesLock, () -> this.nonCompositeDescendants.forEach(((CompositeMeter)m)::add));
            }
        }).onMeterRemoved(m -> {
            if (m instanceof CompositeMeter) {
                this.lock(this.registriesLock, () -> this.nonCompositeDescendants.forEach(r -> r.removeByPreFilterId(m.getId())));
            }
        });
        registries.forEach(this::add);
    }

    @Override
    protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetector) {
        return new CompositeTimer(id, this.clock, distributionStatisticConfig, pauseDetector);
    }

    @Override
    protected DistributionSummary newDistributionSummary(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, double scale) {
        return new CompositeDistributionSummary(id, distributionStatisticConfig, scale);
    }

    @Override
    protected Counter newCounter(Meter.Id id) {
        return new CompositeCounter(id);
    }

    @Override
    protected LongTaskTimer newLongTaskTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig) {
        return new CompositeLongTaskTimer(id, distributionStatisticConfig);
    }

    @Override
    protected <T> Gauge newGauge(Meter.Id id, @Nullable T obj, ToDoubleFunction<T> valueFunction) {
        return new CompositeGauge<T>(id, obj, valueFunction);
    }

    @Override
    protected <T> TimeGauge newTimeGauge(Meter.Id id, @Nullable T obj, TimeUnit valueFunctionUnit, ToDoubleFunction<T> valueFunction) {
        return new CompositeTimeGauge<T>(id, obj, valueFunctionUnit, valueFunction);
    }

    @Override
    protected <T> FunctionTimer newFunctionTimer(Meter.Id id, T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
        return new CompositeFunctionTimer<T>(id, obj, countFunction, totalTimeFunction, totalTimeFunctionUnit);
    }

    @Override
    protected <T> FunctionCounter newFunctionCounter(Meter.Id id, T obj, ToDoubleFunction<T> countFunction) {
        return new CompositeFunctionCounter<T>(id, obj, countFunction);
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.SECONDS;
    }

    @Override
    protected DistributionStatisticConfig defaultHistogramConfig() {
        return DistributionStatisticConfig.NONE;
    }

    @Override
    protected Meter newMeter(Meter.Id id, Meter.Type type, Iterable<Measurement> measurements) {
        return new CompositeCustomMeter(id, type, measurements);
    }

    public CompositeMeterRegistry add(MeterRegistry registry) {
        this.lock(this.registriesLock, () -> {
            this.forbidSelfContainingComposite(registry);
            if (registry instanceof CompositeMeterRegistry) {
                ((CompositeMeterRegistry)registry).addParent(this);
            }
            if (this.registries.add(registry)) {
                this.updateDescendants();
            }
        });
        return this;
    }

    private void forbidSelfContainingComposite(MeterRegistry registry) {
        if (registry == this) {
            throw new IllegalArgumentException("Adding a composite meter registry to itself is not allowed!");
        }
        if (registry instanceof CompositeMeterRegistry) {
            ((CompositeMeterRegistry)registry).getRegistries().forEach(this::forbidSelfContainingComposite);
        }
    }

    public CompositeMeterRegistry remove(MeterRegistry registry) {
        this.lock(this.registriesLock, () -> {
            if (registry instanceof CompositeMeterRegistry) {
                ((CompositeMeterRegistry)registry).removeParent(this);
            }
            if (this.registries.remove(registry)) {
                this.updateDescendants();
            }
        });
        return this;
    }

    private void removeParent(CompositeMeterRegistry registry) {
        this.lock(this.parentLock, () -> this.parents.remove(registry));
    }

    private void addParent(CompositeMeterRegistry registry) {
        this.lock(this.parentLock, () -> this.parents.add(registry));
    }

    private void lock(AtomicBoolean lock, Runnable r) {
        while (!lock.compareAndSet(false, true)) {
        }
        try {
            r.run();
        }
        finally {
            lock.set(false);
        }
    }

    private void updateDescendants() {
        Set descendants = Collections.newSetFromMap(new IdentityHashMap());
        for (MeterRegistry r : this.registries) {
            if (r instanceof CompositeMeterRegistry) {
                descendants.addAll(((CompositeMeterRegistry)r).nonCompositeDescendants);
                continue;
            }
            descendants.add(r);
        }
        Set removes = Collections.newSetFromMap(new IdentityHashMap());
        removes.addAll(this.nonCompositeDescendants);
        removes.removeAll(descendants);
        Set adds = Collections.newSetFromMap(new IdentityHashMap());
        adds.addAll(descendants);
        adds.removeAll(this.nonCompositeDescendants);
        if (!removes.isEmpty() || !adds.isEmpty()) {
            for (Meter meter : this.getMeters()) {
                if (!(meter instanceof CompositeMeter)) continue;
                CompositeMeter composite = (CompositeMeter)meter;
                removes.forEach(composite::remove);
                adds.forEach(composite::add);
            }
        }
        this.nonCompositeDescendants = descendants;
        this.lock(this.parentLock, () -> this.parents.forEach(CompositeMeterRegistry::updateDescendants));
    }

    public Set<MeterRegistry> getRegistries() {
        return this.unmodifiableRegistries;
    }

    @Override
    public void close() {
        this.registries.forEach(MeterRegistry::close);
        super.close();
    }
}

