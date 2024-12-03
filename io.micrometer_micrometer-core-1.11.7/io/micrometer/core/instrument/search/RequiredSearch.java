/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.search;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.search.MeterNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class RequiredSearch {
    final MeterRegistry registry;
    final List<Tag> requiredTags = new ArrayList<Tag>();
    final Set<String> requiredTagKeys = new HashSet<String>();
    @Nullable
    String exactNameMatch;
    @Nullable
    Predicate<String> nameMatches;

    private RequiredSearch(MeterRegistry registry) {
        this.registry = registry;
    }

    public RequiredSearch name(String exactName) {
        this.nameMatches = n -> n.equals(exactName);
        this.exactNameMatch = exactName;
        return this;
    }

    public RequiredSearch name(Predicate<String> nameMatches) {
        this.nameMatches = nameMatches;
        return this;
    }

    public RequiredSearch tags(Iterable<Tag> tags) {
        tags.forEach(this.requiredTags::add);
        return this;
    }

    public RequiredSearch tags(String ... tags) {
        return this.tags(Tags.of(tags));
    }

    public RequiredSearch tag(String tagKey, String tagValue) {
        return this.tags(Tags.of(tagKey, tagValue));
    }

    public RequiredSearch tagKeys(String ... tagKeys) {
        Collections.addAll(this.requiredTagKeys, tagKeys);
        return this;
    }

    public Timer timer() {
        return this.getOne(Timer.class);
    }

    public Counter counter() {
        return this.getOne(Counter.class);
    }

    public Gauge gauge() {
        return this.getOne(Gauge.class);
    }

    public FunctionCounter functionCounter() {
        return this.getOne(FunctionCounter.class);
    }

    public TimeGauge timeGauge() {
        return this.getOne(TimeGauge.class);
    }

    public FunctionTimer functionTimer() {
        return this.getOne(FunctionTimer.class);
    }

    public DistributionSummary summary() {
        return this.getOne(DistributionSummary.class);
    }

    public LongTaskTimer longTaskTimer() {
        return this.getOne(LongTaskTimer.class);
    }

    public Meter meter() {
        return this.getOne(Meter.class);
    }

    private <M extends Meter> M getOne(Class<M> clazz) {
        return (M)this.meterStream().filter(clazz::isInstance).findAny().map(clazz::cast).orElseThrow(() -> MeterNotFoundException.forSearch(this, clazz));
    }

    private <M extends Meter> Collection<M> findAll(Class<M> clazz) {
        List meters = this.meterStream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
        if (meters.isEmpty()) {
            throw MeterNotFoundException.forSearch(this, clazz);
        }
        return meters;
    }

    public Collection<Meter> meters() {
        List<Meter> meters = this.meterStream().collect(Collectors.toList());
        if (meters.isEmpty()) {
            throw MeterNotFoundException.forSearch(this, Meter.class);
        }
        return meters;
    }

    private Stream<Meter> meterStream() {
        Stream<Meter> meterStream = this.registry.getMeters().stream().filter(m -> this.nameMatches == null || this.nameMatches.test(m.getId().getName()));
        if (!this.requiredTags.isEmpty() || !this.requiredTagKeys.isEmpty()) {
            meterStream = meterStream.filter(m -> {
                boolean requiredKeysPresent = true;
                if (!this.requiredTagKeys.isEmpty()) {
                    ArrayList tagKeys = new ArrayList();
                    m.getId().getTagsAsIterable().forEach(t -> tagKeys.add(t.getKey()));
                    requiredKeysPresent = tagKeys.containsAll(this.requiredTagKeys);
                }
                return requiredKeysPresent && m.getId().getTags().containsAll(this.requiredTags);
            });
        }
        return meterStream;
    }

    public Collection<Counter> counters() {
        return this.findAll(Counter.class);
    }

    public Collection<Gauge> gauges() {
        return this.findAll(Gauge.class);
    }

    public Collection<Timer> timers() {
        return this.findAll(Timer.class);
    }

    public Collection<DistributionSummary> summaries() {
        return this.findAll(DistributionSummary.class);
    }

    public Collection<LongTaskTimer> longTaskTimers() {
        return this.findAll(LongTaskTimer.class);
    }

    public Collection<FunctionCounter> functionCounters() {
        return this.findAll(FunctionCounter.class);
    }

    public Collection<FunctionTimer> functionTimers() {
        return this.findAll(FunctionTimer.class);
    }

    public Collection<TimeGauge> timeGauges() {
        return this.findAll(TimeGauge.class);
    }

    public static RequiredSearch in(MeterRegistry registry) {
        return new RequiredSearch(registry);
    }
}

