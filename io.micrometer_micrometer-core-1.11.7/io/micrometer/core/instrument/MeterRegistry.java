/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.HighCardinalityTagsDetector;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.NoPauseDetector;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.noop.NoopCounter;
import io.micrometer.core.instrument.noop.NoopDistributionSummary;
import io.micrometer.core.instrument.noop.NoopFunctionCounter;
import io.micrometer.core.instrument.noop.NoopFunctionTimer;
import io.micrometer.core.instrument.noop.NoopGauge;
import io.micrometer.core.instrument.noop.NoopLongTaskTimer;
import io.micrometer.core.instrument.noop.NoopMeter;
import io.micrometer.core.instrument.noop.NoopTimeGauge;
import io.micrometer.core.instrument.noop.NoopTimer;
import io.micrometer.core.instrument.search.RequiredSearch;
import io.micrometer.core.instrument.search.Search;
import io.micrometer.core.instrument.util.TimeUtils;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

public abstract class MeterRegistry {
    private static final EnumMap<TimeUnit, String> BASE_TIME_UNIT_STRING_CACHE = Arrays.stream(TimeUnit.values()).collect(Collectors.toMap(Function.identity(), timeUnit -> timeUnit.toString().toLowerCase(), (k, v) -> {
        throw new IllegalStateException("Duplicate keys should not exist.");
    }, () -> new EnumMap(TimeUnit.class)));
    protected final Clock clock;
    private final Object meterMapLock = new Object();
    private volatile MeterFilter[] filters = new MeterFilter[0];
    private final List<Consumer<Meter>> meterAddedListeners = new CopyOnWriteArrayList<Consumer<Meter>>();
    private final List<Consumer<Meter>> meterRemovedListeners = new CopyOnWriteArrayList<Consumer<Meter>>();
    private final List<BiConsumer<Meter.Id, String>> meterRegistrationFailedListeners = new CopyOnWriteArrayList<BiConsumer<Meter.Id, String>>();
    private final Config config = new Config();
    private final More more = new More();
    private final Map<Meter.Id, Meter> meterMap = new ConcurrentHashMap<Meter.Id, Meter>();
    private final Map<Meter.Id, Set<Meter.Id>> syntheticAssociations = new HashMap<Meter.Id, Set<Meter.Id>>();
    private final AtomicBoolean closed = new AtomicBoolean();
    private PauseDetector pauseDetector = new NoPauseDetector();
    @Nullable
    private HighCardinalityTagsDetector highCardinalityTagsDetector;
    private NamingConvention namingConvention = NamingConvention.snakeCase;

    protected MeterRegistry(Clock clock) {
        Objects.requireNonNull(clock);
        this.clock = clock;
    }

    protected abstract <T> Gauge newGauge(Meter.Id var1, @Nullable T var2, ToDoubleFunction<T> var3);

    protected abstract Counter newCounter(Meter.Id var1);

    @Deprecated
    protected LongTaskTimer newLongTaskTimer(Meter.Id id) {
        throw new UnsupportedOperationException("MeterRegistry implementations may still override this, but it is only invoked by the overloaded form of newLongTaskTimer for backwards compatibility.");
    }

    protected LongTaskTimer newLongTaskTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig) {
        return this.newLongTaskTimer(id);
    }

    protected abstract Timer newTimer(Meter.Id var1, DistributionStatisticConfig var2, PauseDetector var3);

    protected abstract DistributionSummary newDistributionSummary(Meter.Id var1, DistributionStatisticConfig var2, double var3);

    protected abstract Meter newMeter(Meter.Id var1, Meter.Type var2, Iterable<Measurement> var3);

    protected <T> TimeGauge newTimeGauge(Meter.Id id, @Nullable T obj, TimeUnit valueFunctionUnit, ToDoubleFunction<T> valueFunction) {
        final Meter.Id withUnit = id.withBaseUnit(this.getBaseTimeUnitStr());
        final Gauge gauge = this.newGauge(withUnit, obj, obj2 -> TimeUtils.convert(valueFunction.applyAsDouble(obj2), valueFunctionUnit, this.getBaseTimeUnit()));
        return new TimeGauge(){

            @Override
            public Meter.Id getId() {
                return withUnit;
            }

            @Override
            public double value() {
                return gauge.value();
            }

            @Override
            public TimeUnit baseTimeUnit() {
                return MeterRegistry.this.getBaseTimeUnit();
            }
        };
    }

    protected abstract <T> FunctionTimer newFunctionTimer(Meter.Id var1, T var2, ToLongFunction<T> var3, ToDoubleFunction<T> var4, TimeUnit var5);

    protected abstract <T> FunctionCounter newFunctionCounter(Meter.Id var1, T var2, ToDoubleFunction<T> var3);

    protected List<Tag> getConventionTags(Meter.Id id) {
        return id.getConventionTags(this.config().namingConvention());
    }

    protected String getConventionName(Meter.Id id) {
        return id.getConventionName(this.config().namingConvention());
    }

    protected abstract TimeUnit getBaseTimeUnit();

    protected abstract DistributionStatisticConfig defaultHistogramConfig();

    private String getBaseTimeUnitStr() {
        return BASE_TIME_UNIT_STRING_CACHE.get((Object)this.getBaseTimeUnit());
    }

    Counter counter(Meter.Id id) {
        return this.registerMeterIfNecessary(Counter.class, id, this::newCounter, NoopCounter::new);
    }

    <T> Gauge gauge(Meter.Id id, @Nullable T obj, ToDoubleFunction<T> valueFunction) {
        return this.registerMeterIfNecessary(Gauge.class, id, id2 -> this.newGauge((Meter.Id)id2, obj, valueFunction), NoopGauge::new);
    }

    Timer timer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, PauseDetector pauseDetectorOverride) {
        return this.registerMeterIfNecessary(Timer.class, id, distributionStatisticConfig, (id2, filteredConfig) -> {
            Meter.Id withUnit = id2.withBaseUnit(this.getBaseTimeUnitStr());
            return this.newTimer(withUnit, filteredConfig.merge(this.defaultHistogramConfig()), pauseDetectorOverride);
        }, NoopTimer::new);
    }

    DistributionSummary summary(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, double scale) {
        return this.registerMeterIfNecessary(DistributionSummary.class, id, distributionStatisticConfig, (id2, filteredConfig) -> this.newDistributionSummary((Meter.Id)id2, filteredConfig.merge(this.defaultHistogramConfig()), scale), NoopDistributionSummary::new);
    }

    Meter register(Meter.Id id, Meter.Type type, Iterable<Measurement> measurements) {
        return this.registerMeterIfNecessary(Meter.class, id, id2 -> this.newMeter((Meter.Id)id2, type, measurements), NoopMeter::new);
    }

    public List<Meter> getMeters() {
        return Collections.unmodifiableList(new ArrayList<Meter>(this.meterMap.values()));
    }

    public void forEachMeter(Consumer<? super Meter> consumer) {
        this.meterMap.values().forEach(consumer);
    }

    public Config config() {
        return this.config;
    }

    public Search find(String name) {
        return Search.in(this).name(name);
    }

    public RequiredSearch get(String name) {
        return RequiredSearch.in(this).name(name);
    }

    public Counter counter(String name, Iterable<Tag> tags) {
        return Counter.builder(name).tags(tags).register(this);
    }

    public Counter counter(String name, String ... tags) {
        return this.counter(name, Tags.of(tags));
    }

    public DistributionSummary summary(String name, Iterable<Tag> tags) {
        return DistributionSummary.builder(name).tags(tags).register(this);
    }

    public DistributionSummary summary(String name, String ... tags) {
        return this.summary(name, Tags.of(tags));
    }

    public Timer timer(String name, Iterable<Tag> tags) {
        return ((Timer.Builder)Timer.builder(name).tags((Iterable)tags)).register(this);
    }

    public Timer timer(String name, String ... tags) {
        return this.timer(name, Tags.of(tags));
    }

    public More more() {
        return this.more;
    }

    @Nullable
    public <T> T gauge(String name, Iterable<Tag> tags, @Nullable T stateObject, ToDoubleFunction<T> valueFunction) {
        Gauge.builder(name, stateObject, valueFunction).tags(tags).register(this);
        return stateObject;
    }

    @Nullable
    public <T extends Number> T gauge(String name, Iterable<Tag> tags, T number) {
        return (T)this.gauge(name, tags, number, Number::doubleValue);
    }

    @Nullable
    public <T extends Number> T gauge(String name, T number) {
        return this.gauge(name, Collections.emptyList(), number);
    }

    @Nullable
    public <T> T gauge(String name, T stateObject, ToDoubleFunction<T> valueFunction) {
        return this.gauge(name, Collections.emptyList(), stateObject, valueFunction);
    }

    @Nullable
    public <T extends Collection<?>> T gaugeCollectionSize(String name, Iterable<Tag> tags, T collection) {
        return (T)this.gauge(name, tags, collection, Collection::size);
    }

    @Nullable
    public <T extends Map<?, ?>> T gaugeMapSize(String name, Iterable<Tag> tags, T map) {
        return (T)this.gauge(name, tags, map, Map::size);
    }

    private <M extends Meter> M registerMeterIfNecessary(Class<M> meterClass, Meter.Id id, Function<Meter.Id, M> builder, Function<Meter.Id, M> noopBuilder) {
        return (M)this.registerMeterIfNecessary(meterClass, id, null, (id2, conf) -> (Meter)builder.apply((Meter.Id)id2), noopBuilder);
    }

    private <M extends Meter> M registerMeterIfNecessary(Class<M> meterClass, Meter.Id id, @Nullable DistributionStatisticConfig config, BiFunction<Meter.Id, DistributionStatisticConfig, M> builder, Function<Meter.Id, M> noopBuilder) {
        Meter.Id mappedId = this.getMappedId(id);
        Meter m = this.getOrCreateMeter(config, builder, id, mappedId, noopBuilder);
        if (!meterClass.isInstance(m)) {
            throw new IllegalArgumentException(String.format("There is already a registered meter of a different type (%s vs. %s) with the same name: %s", m.getClass().getSimpleName(), meterClass.getSimpleName(), id.getName()));
        }
        return (M)((Meter)meterClass.cast(m));
    }

    private Meter.Id getMappedId(Meter.Id id) {
        if (id.syntheticAssociation() != null) {
            return id;
        }
        Meter.Id mappedId = id;
        for (MeterFilter filter : this.filters) {
            mappedId = filter.map(mappedId);
        }
        return mappedId;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Meter getOrCreateMeter(@Nullable DistributionStatisticConfig config, BiFunction<Meter.Id, DistributionStatisticConfig, ? extends Meter> builder, Meter.Id originalId, Meter.Id mappedId, Function<Meter.Id, ? extends Meter> noopBuilder) {
        Meter m = this.meterMap.get(mappedId);
        if (m == null) {
            if (this.isClosed()) {
                return noopBuilder.apply(mappedId);
            }
            Object object = this.meterMapLock;
            synchronized (object) {
                m = this.meterMap.get(mappedId);
                if (m == null) {
                    if (!this.accept(mappedId)) {
                        return noopBuilder.apply(mappedId);
                    }
                    if (config != null) {
                        for (MeterFilter filter : this.filters) {
                            DistributionStatisticConfig filteredConfig = filter.configure(mappedId, config);
                            if (filteredConfig == null) continue;
                            config = filteredConfig;
                        }
                    }
                    m = builder.apply(mappedId, config);
                    Meter.Id synAssoc = mappedId.syntheticAssociation();
                    if (synAssoc != null) {
                        Set associations = this.syntheticAssociations.computeIfAbsent(synAssoc, k -> new HashSet());
                        associations.add(mappedId);
                    }
                    for (Consumer<Meter> onAdd : this.meterAddedListeners) {
                        onAdd.accept(m);
                    }
                    this.meterMap.put(mappedId, m);
                }
            }
        }
        return m;
    }

    private boolean accept(Meter.Id id) {
        for (MeterFilter filter : this.filters) {
            MeterFilterReply reply = filter.accept(id);
            if (reply == MeterFilterReply.DENY) {
                return false;
            }
            if (reply != MeterFilterReply.ACCEPT) continue;
            return true;
        }
        return true;
    }

    @Nullable
    @Incubating(since="1.1.0")
    public Meter remove(Meter meter) {
        return this.remove(meter.getId());
    }

    @Nullable
    @Incubating(since="1.3.16")
    public Meter removeByPreFilterId(Meter.Id preFilterId) {
        return this.remove(this.getMappedId(preFilterId));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    @Incubating(since="1.1.0")
    public Meter remove(Meter.Id mappedId) {
        Meter m = this.meterMap.get(mappedId);
        if (m != null) {
            Object object = this.meterMapLock;
            synchronized (object) {
                m = this.meterMap.remove(mappedId);
                if (m != null) {
                    Set<Meter.Id> synthetics = this.syntheticAssociations.remove(mappedId);
                    if (synthetics != null) {
                        for (Meter.Id id : synthetics) {
                            this.remove(id);
                        }
                    }
                    for (Consumer consumer : this.meterRemovedListeners) {
                        consumer.accept(m);
                    }
                    return m;
                }
            }
        }
        return null;
    }

    @Incubating(since="1.2.0")
    public void clear() {
        this.meterMap.keySet().forEach(this::remove);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() {
        if (this.closed.compareAndSet(false, true)) {
            Object object = this.meterMapLock;
            synchronized (object) {
                for (Meter meter : this.meterMap.values()) {
                    meter.close();
                }
            }
        }
        if (this.highCardinalityTagsDetector != null) {
            this.highCardinalityTagsDetector.close();
        }
    }

    public boolean isClosed() {
        return this.closed.get();
    }

    protected void meterRegistrationFailed(Meter.Id id, @Nullable String reason) {
        for (BiConsumer<Meter.Id, String> listener : this.meterRegistrationFailedListeners) {
            listener.accept(id, reason);
        }
    }

    static /* synthetic */ MeterFilter[] access$002(MeterRegistry x0, MeterFilter[] x1) {
        x0.filters = x1;
        return x1;
    }

    public class Config {
        public Config commonTags(Iterable<Tag> tags) {
            return this.meterFilter(MeterFilter.commonTags(tags));
        }

        public Config commonTags(String ... tags) {
            return this.commonTags(Tags.of(tags));
        }

        public synchronized Config meterFilter(MeterFilter filter) {
            MeterFilter[] newFilters = new MeterFilter[MeterRegistry.this.filters.length + 1];
            System.arraycopy(MeterRegistry.this.filters, 0, newFilters, 0, MeterRegistry.this.filters.length);
            newFilters[((MeterRegistry)MeterRegistry.this).filters.length] = filter;
            MeterRegistry.access$002(MeterRegistry.this, newFilters);
            return this;
        }

        public Config onMeterAdded(Consumer<Meter> meterAddedListener) {
            MeterRegistry.this.meterAddedListeners.add(meterAddedListener);
            return this;
        }

        @Incubating(since="1.1.0")
        public Config onMeterRemoved(Consumer<Meter> meterRemovedListener) {
            MeterRegistry.this.meterRemovedListeners.add(meterRemovedListener);
            return this;
        }

        @Incubating(since="1.6.0")
        public Config onMeterRegistrationFailed(BiConsumer<Meter.Id, String> meterRegistrationFailedListener) {
            MeterRegistry.this.meterRegistrationFailedListeners.add(meterRegistrationFailedListener);
            return this;
        }

        public Config namingConvention(NamingConvention convention) {
            MeterRegistry.this.namingConvention = convention;
            return this;
        }

        public NamingConvention namingConvention() {
            return MeterRegistry.this.namingConvention;
        }

        public Clock clock() {
            return MeterRegistry.this.clock;
        }

        public Config pauseDetector(PauseDetector detector) {
            MeterRegistry.this.pauseDetector = detector;
            return this;
        }

        public PauseDetector pauseDetector() {
            return MeterRegistry.this.pauseDetector;
        }

        public Config withHighCardinalityTagsDetector() {
            return this.withHighCardinalityTagsDetector(new HighCardinalityTagsDetector(MeterRegistry.this));
        }

        public Config withHighCardinalityTagsDetector(long threshold, Duration delay) {
            return this.withHighCardinalityTagsDetector(new HighCardinalityTagsDetector(MeterRegistry.this, threshold, delay));
        }

        private Config withHighCardinalityTagsDetector(HighCardinalityTagsDetector newHighCardinalityTagsDetector) {
            if (MeterRegistry.this.highCardinalityTagsDetector != null) {
                MeterRegistry.this.highCardinalityTagsDetector.close();
            }
            MeterRegistry.this.highCardinalityTagsDetector = newHighCardinalityTagsDetector;
            MeterRegistry.this.highCardinalityTagsDetector.start();
            return this;
        }

        @Nullable
        public HighCardinalityTagsDetector highCardinalityTagsDetector() {
            return MeterRegistry.this.highCardinalityTagsDetector;
        }
    }

    public class More {
        public LongTaskTimer longTaskTimer(String name, String ... tags) {
            return this.longTaskTimer(name, Tags.of(tags));
        }

        public LongTaskTimer longTaskTimer(String name, Iterable<Tag> tags) {
            return LongTaskTimer.builder(name).tags(tags).register(MeterRegistry.this);
        }

        LongTaskTimer longTaskTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig) {
            return (LongTaskTimer)MeterRegistry.this.registerMeterIfNecessary(LongTaskTimer.class, id, distributionStatisticConfig, (id2, filteredConfig) -> {
                Meter.Id withUnit = id2.withBaseUnit(MeterRegistry.this.getBaseTimeUnitStr());
                return MeterRegistry.this.newLongTaskTimer(withUnit, filteredConfig.merge(MeterRegistry.this.defaultHistogramConfig()));
            }, NoopLongTaskTimer::new);
        }

        public <T> FunctionCounter counter(String name, Iterable<Tag> tags, T obj, ToDoubleFunction<T> countFunction) {
            return FunctionCounter.builder(name, obj, countFunction).tags(tags).register(MeterRegistry.this);
        }

        public <T extends Number> FunctionCounter counter(String name, Iterable<Tag> tags, T number) {
            return FunctionCounter.builder(name, number, Number::doubleValue).tags(tags).register(MeterRegistry.this);
        }

        <T> FunctionCounter counter(Meter.Id id, T obj, ToDoubleFunction<T> countFunction) {
            return (FunctionCounter)MeterRegistry.this.registerMeterIfNecessary(FunctionCounter.class, id, id2 -> MeterRegistry.this.newFunctionCounter((Meter.Id)id2, obj, countFunction), NoopFunctionCounter::new);
        }

        public <T> FunctionTimer timer(String name, Iterable<Tag> tags, T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
            return FunctionTimer.builder(name, obj, countFunction, totalTimeFunction, totalTimeFunctionUnit).tags(tags).register(MeterRegistry.this);
        }

        <T> FunctionTimer timer(Meter.Id id, T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit) {
            return (FunctionTimer)MeterRegistry.this.registerMeterIfNecessary(FunctionTimer.class, id, id2 -> {
                Meter.Id withUnit = id2.withBaseUnit(MeterRegistry.this.getBaseTimeUnitStr());
                return MeterRegistry.this.newFunctionTimer(withUnit, obj, countFunction, totalTimeFunction, totalTimeFunctionUnit);
            }, NoopFunctionTimer::new);
        }

        public <T> TimeGauge timeGauge(String name, Iterable<Tag> tags, T obj, TimeUnit timeFunctionUnit, ToDoubleFunction<T> timeFunction) {
            return TimeGauge.builder(name, obj, timeFunctionUnit, timeFunction).tags(tags).register(MeterRegistry.this);
        }

        <T> TimeGauge timeGauge(Meter.Id id, @Nullable T obj, TimeUnit timeFunctionUnit, ToDoubleFunction<T> timeFunction) {
            return (TimeGauge)MeterRegistry.this.registerMeterIfNecessary(TimeGauge.class, id, id2 -> MeterRegistry.this.newTimeGauge((Meter.Id)id2, obj, timeFunctionUnit, timeFunction), NoopTimeGauge::new);
        }
    }
}

