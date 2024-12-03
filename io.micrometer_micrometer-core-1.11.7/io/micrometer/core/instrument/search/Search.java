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
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Search {
    private final MeterRegistry registry;
    private final List<Tag> tags = new ArrayList<Tag>();
    private Predicate<String> nameMatches = n -> true;
    private final Set<String> requiredTagKeys = new HashSet<String>();
    private final Map<String, Collection<Predicate<String>>> tagMatches = new HashMap<String, Collection<Predicate<String>>>();

    private Search(MeterRegistry registry) {
        this.registry = registry;
    }

    public Search name(String exactName) {
        return this.name((String n) -> n.equals(exactName));
    }

    public Search name(@Nullable Predicate<String> nameMatches) {
        if (nameMatches != null) {
            this.nameMatches = nameMatches;
        }
        return this;
    }

    public Search tags(Iterable<Tag> tags) {
        tags.forEach(this.tags::add);
        return this;
    }

    public Search tags(String ... tags) {
        return this.tags(Tags.of(tags));
    }

    public Search tag(String tagKey, String tagValue) {
        return this.tags(Tags.of(tagKey, tagValue));
    }

    public Search tagKeys(String ... tagKeys) {
        return this.tagKeys(Arrays.asList(tagKeys));
    }

    public Search tagKeys(Collection<String> tagKeys) {
        this.requiredTagKeys.addAll(tagKeys);
        return this;
    }

    public Search tag(String tagKey, Predicate<String> tagValueMatches) {
        this.tagMatches.computeIfAbsent(tagKey, k -> new ArrayList()).add(tagValueMatches);
        return this;
    }

    @Nullable
    public Timer timer() {
        return this.findOne(Timer.class);
    }

    @Nullable
    public Counter counter() {
        return this.findOne(Counter.class);
    }

    @Nullable
    public Gauge gauge() {
        return this.findOne(Gauge.class);
    }

    @Nullable
    public FunctionCounter functionCounter() {
        return this.findOne(FunctionCounter.class);
    }

    @Nullable
    public TimeGauge timeGauge() {
        return this.findOne(TimeGauge.class);
    }

    @Nullable
    public FunctionTimer functionTimer() {
        return this.findOne(FunctionTimer.class);
    }

    @Nullable
    public DistributionSummary summary() {
        return this.findOne(DistributionSummary.class);
    }

    @Nullable
    public LongTaskTimer longTaskTimer() {
        return this.findOne(LongTaskTimer.class);
    }

    @Nullable
    public Meter meter() {
        return this.findOne(Meter.class);
    }

    @Nullable
    private <M extends Meter> M findOne(Class<M> clazz) {
        return (M)((Meter)this.meterStream().filter(clazz::isInstance).findAny().map(clazz::cast).orElse(null));
    }

    public Collection<Meter> meters() {
        return this.meterStream().collect(Collectors.toList());
    }

    public MeterFilter acceptFilter() {
        return new MeterFilter(){

            @Override
            public MeterFilterReply accept(Meter.Id id) {
                if (!Search.this.nameMatches.test(id.getName())) {
                    return MeterFilterReply.NEUTRAL;
                }
                return Search.this.isTagsMatched(id) ? MeterFilterReply.ACCEPT : MeterFilterReply.NEUTRAL;
            }
        };
    }

    private boolean isTagsMatched(Meter.Id id) {
        return this.isRequiredTagKeysPresent(id) && this.isTagPredicatesMatched(id) && id.getTags().containsAll(this.tags);
    }

    private boolean isRequiredTagKeysPresent(Meter.Id id) {
        if (!this.requiredTagKeys.isEmpty()) {
            HashSet tagKeys = new HashSet();
            id.getTags().forEach(t -> tagKeys.add(t.getKey()));
            return tagKeys.containsAll(this.requiredTagKeys);
        }
        return true;
    }

    private boolean isTagPredicatesMatched(Meter.Id id) {
        if (!this.tagMatches.isEmpty()) {
            HashSet matchingTagKeys = new HashSet();
            id.getTags().forEach(t -> {
                Collection<Predicate<String>> tagValueMatchers = this.tagMatches.get(t.getKey());
                if (tagValueMatchers != null && tagValueMatchers.stream().allMatch(matcher -> matcher.test(t.getValue()))) {
                    matchingTagKeys.add(t.getKey());
                }
            });
            return this.tagMatches.keySet().size() == matchingTagKeys.size();
        }
        return true;
    }

    private Stream<Meter> meterStream() {
        Stream<Meter> meterStream = this.registry.getMeters().stream().filter(m -> this.nameMatches.test(m.getId().getName()));
        if (!(this.tags.isEmpty() && this.requiredTagKeys.isEmpty() && this.tagMatches.isEmpty())) {
            meterStream = meterStream.filter(m -> this.isTagsMatched(m.getId()));
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

    private <M extends Meter> Collection<M> findAll(Class<M> clazz) {
        return this.meterStream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
    }

    public static Search in(MeterRegistry registry) {
        return new Search(registry);
    }
}

