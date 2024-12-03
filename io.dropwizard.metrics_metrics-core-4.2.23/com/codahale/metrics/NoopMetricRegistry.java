/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricRegistryListener;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.Reservoir;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class NoopMetricRegistry
extends MetricRegistry {
    private static final EmptyConcurrentMap<String, Metric> EMPTY_CONCURRENT_MAP = new EmptyConcurrentMap();

    @Override
    protected ConcurrentMap<String, Metric> buildMap() {
        return EMPTY_CONCURRENT_MAP;
    }

    @Override
    public <T extends Metric> T register(String name, T metric) throws IllegalArgumentException {
        if (metric == null) {
            throw new NullPointerException("metric == null");
        }
        return metric;
    }

    @Override
    public void registerAll(MetricSet metrics) throws IllegalArgumentException {
    }

    @Override
    public Counter counter(String name) {
        return NoopCounter.INSTANCE;
    }

    @Override
    public Counter counter(String name, MetricRegistry.MetricSupplier<Counter> supplier) {
        return NoopCounter.INSTANCE;
    }

    @Override
    public Histogram histogram(String name) {
        return NoopHistogram.INSTANCE;
    }

    @Override
    public Histogram histogram(String name, MetricRegistry.MetricSupplier<Histogram> supplier) {
        return NoopHistogram.INSTANCE;
    }

    @Override
    public Meter meter(String name) {
        return NoopMeter.INSTANCE;
    }

    @Override
    public Meter meter(String name, MetricRegistry.MetricSupplier<Meter> supplier) {
        return NoopMeter.INSTANCE;
    }

    @Override
    public Timer timer(String name) {
        return NoopTimer.INSTANCE;
    }

    @Override
    public Timer timer(String name, MetricRegistry.MetricSupplier<Timer> supplier) {
        return NoopTimer.INSTANCE;
    }

    @Override
    public <T extends Gauge> T gauge(String name) {
        return (T)NoopGauge.INSTANCE;
    }

    @Override
    public <T extends Gauge> T gauge(String name, MetricRegistry.MetricSupplier<T> supplier) {
        return (T)NoopGauge.INSTANCE;
    }

    @Override
    public boolean remove(String name) {
        return false;
    }

    @Override
    public void removeMatching(MetricFilter filter) {
    }

    @Override
    public void addListener(MetricRegistryListener listener) {
    }

    @Override
    public void removeListener(MetricRegistryListener listener) {
    }

    @Override
    public SortedSet<String> getNames() {
        return Collections.emptySortedSet();
    }

    @Override
    public SortedMap<String, Gauge> getGauges() {
        return Collections.emptySortedMap();
    }

    @Override
    public SortedMap<String, Gauge> getGauges(MetricFilter filter) {
        return Collections.emptySortedMap();
    }

    @Override
    public SortedMap<String, Counter> getCounters() {
        return Collections.emptySortedMap();
    }

    @Override
    public SortedMap<String, Counter> getCounters(MetricFilter filter) {
        return Collections.emptySortedMap();
    }

    @Override
    public SortedMap<String, Histogram> getHistograms() {
        return Collections.emptySortedMap();
    }

    @Override
    public SortedMap<String, Histogram> getHistograms(MetricFilter filter) {
        return Collections.emptySortedMap();
    }

    @Override
    public SortedMap<String, Meter> getMeters() {
        return Collections.emptySortedMap();
    }

    @Override
    public SortedMap<String, Meter> getMeters(MetricFilter filter) {
        return Collections.emptySortedMap();
    }

    @Override
    public SortedMap<String, Timer> getTimers() {
        return Collections.emptySortedMap();
    }

    @Override
    public SortedMap<String, Timer> getTimers(MetricFilter filter) {
        return Collections.emptySortedMap();
    }

    @Override
    public void registerAll(String prefix, MetricSet metrics) throws IllegalArgumentException {
    }

    @Override
    public Map<String, Metric> getMetrics() {
        return Collections.emptyMap();
    }

    private static final class EmptyConcurrentMap<K, V>
    implements ConcurrentMap<K, V> {
        private EmptyConcurrentMap() {
        }

        @Override
        public V putIfAbsent(K key, V value) {
            return null;
        }

        @Override
        public boolean remove(Object key, Object value) {
            return false;
        }

        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            return false;
        }

        @Override
        public V replace(K key, V value) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public V get(Object key) {
            return null;
        }

        @Override
        public V put(K key, V value) {
            return null;
        }

        @Override
        public V remove(Object key) {
            return null;
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
        }

        @Override
        public void clear() {
        }

        @Override
        public Set<K> keySet() {
            return Collections.emptySet();
        }

        @Override
        public Collection<V> values() {
            return Collections.emptySet();
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return Collections.emptySet();
        }
    }

    static final class NoopCounter
    extends Counter {
        private static final NoopCounter INSTANCE = new NoopCounter();

        NoopCounter() {
        }

        @Override
        public void inc() {
        }

        @Override
        public void inc(long n) {
        }

        @Override
        public void dec() {
        }

        @Override
        public void dec(long n) {
        }

        @Override
        public long getCount() {
            return 0L;
        }
    }

    static final class NoopHistogram
    extends Histogram {
        private static final NoopHistogram INSTANCE = new NoopHistogram();
        private static final Reservoir EMPTY_RESERVOIR = new Reservoir(){

            @Override
            public int size() {
                return 0;
            }

            @Override
            public void update(long value) {
            }

            @Override
            public Snapshot getSnapshot() {
                return EmptySnapshot.INSTANCE;
            }
        };

        private NoopHistogram() {
            super(EMPTY_RESERVOIR);
        }

        @Override
        public void update(int value) {
        }

        @Override
        public void update(long value) {
        }

        @Override
        public long getCount() {
            return 0L;
        }

        @Override
        public Snapshot getSnapshot() {
            return EmptySnapshot.INSTANCE;
        }
    }

    static final class NoopMeter
    extends Meter {
        private static final NoopMeter INSTANCE = new NoopMeter();

        NoopMeter() {
        }

        @Override
        public void mark() {
        }

        @Override
        public void mark(long n) {
        }

        @Override
        public long getCount() {
            return 0L;
        }

        @Override
        public double getFifteenMinuteRate() {
            return 0.0;
        }

        @Override
        public double getFiveMinuteRate() {
            return 0.0;
        }

        @Override
        public double getMeanRate() {
            return 0.0;
        }

        @Override
        public double getOneMinuteRate() {
            return 0.0;
        }
    }

    static final class NoopTimer
    extends Timer {
        private static final NoopTimer INSTANCE = new NoopTimer();
        private static final Timer.Context CONTEXT = new Context();

        NoopTimer() {
        }

        @Override
        public void update(long duration, TimeUnit unit) {
        }

        @Override
        public void update(Duration duration) {
        }

        @Override
        public <T> T time(Callable<T> event) throws Exception {
            return event.call();
        }

        @Override
        public <T> T timeSupplier(Supplier<T> event) {
            return event.get();
        }

        @Override
        public void time(Runnable event) {
        }

        @Override
        public Timer.Context time() {
            return CONTEXT;
        }

        @Override
        public long getCount() {
            return 0L;
        }

        @Override
        public double getFifteenMinuteRate() {
            return 0.0;
        }

        @Override
        public double getFiveMinuteRate() {
            return 0.0;
        }

        @Override
        public double getMeanRate() {
            return 0.0;
        }

        @Override
        public double getOneMinuteRate() {
            return 0.0;
        }

        @Override
        public Snapshot getSnapshot() {
            return EmptySnapshot.INSTANCE;
        }

        private static class Context
        extends Timer.Context {
            private static final Clock CLOCK = new Clock(){

                @Override
                public long getTick() {
                    return 0L;
                }

                @Override
                public long getTime() {
                    return 0L;
                }
            };

            private Context() {
                super(INSTANCE, CLOCK);
            }

            @Override
            public long stop() {
                return 0L;
            }

            @Override
            public void close() {
            }
        }
    }

    static final class NoopGauge<T>
    implements Gauge<T> {
        private static final NoopGauge<?> INSTANCE = new NoopGauge();

        NoopGauge() {
        }

        @Override
        public T getValue() {
            return null;
        }
    }

    private static final class EmptySnapshot
    extends Snapshot {
        private static final EmptySnapshot INSTANCE = new EmptySnapshot();
        private static final long[] EMPTY_LONG_ARRAY = new long[0];

        private EmptySnapshot() {
        }

        @Override
        public double getValue(double quantile) {
            return 0.0;
        }

        @Override
        public long[] getValues() {
            return EMPTY_LONG_ARRAY;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public long getMax() {
            return 0L;
        }

        @Override
        public double getMean() {
            return 0.0;
        }

        @Override
        public long getMin() {
            return 0L;
        }

        @Override
        public double getStdDev() {
            return 0.0;
        }

        @Override
        public void dump(OutputStream output) {
        }
    }
}

