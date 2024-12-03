/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.DefaultSettableGauge;
import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistryListener;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.Timer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MetricRegistry
implements MetricSet {
    private final ConcurrentMap<String, Metric> metrics = this.buildMap();
    private final List<MetricRegistryListener> listeners = new CopyOnWriteArrayList<MetricRegistryListener>();

    public static String name(String name, String ... names) {
        StringBuilder builder = new StringBuilder();
        MetricRegistry.append(builder, name);
        if (names != null) {
            for (String s : names) {
                MetricRegistry.append(builder, s);
            }
        }
        return builder.toString();
    }

    public static String name(Class<?> klass, String ... names) {
        return MetricRegistry.name(klass.getName(), names);
    }

    private static void append(StringBuilder builder, String part) {
        if (part != null && !part.isEmpty()) {
            if (builder.length() > 0) {
                builder.append('.');
            }
            builder.append(part);
        }
    }

    protected ConcurrentMap<String, Metric> buildMap() {
        return new ConcurrentHashMap<String, Metric>();
    }

    public <T> Gauge<T> registerGauge(String name, Gauge<T> metric) throws IllegalArgumentException {
        return this.register(name, metric);
    }

    public <T extends Metric> T register(String name, T metric) throws IllegalArgumentException {
        if (metric == null) {
            throw new NullPointerException("metric == null");
        }
        if (metric instanceof MetricRegistry) {
            MetricRegistry childRegistry = (MetricRegistry)metric;
            final String childName = name;
            childRegistry.addListener(new MetricRegistryListener(){

                @Override
                public void onGaugeAdded(String name, Gauge<?> gauge) {
                    MetricRegistry.this.register(MetricRegistry.name(childName, name), gauge);
                }

                @Override
                public void onGaugeRemoved(String name) {
                    MetricRegistry.this.remove(MetricRegistry.name(childName, name));
                }

                @Override
                public void onCounterAdded(String name, Counter counter) {
                    MetricRegistry.this.register(MetricRegistry.name(childName, name), counter);
                }

                @Override
                public void onCounterRemoved(String name) {
                    MetricRegistry.this.remove(MetricRegistry.name(childName, name));
                }

                @Override
                public void onHistogramAdded(String name, Histogram histogram) {
                    MetricRegistry.this.register(MetricRegistry.name(childName, name), histogram);
                }

                @Override
                public void onHistogramRemoved(String name) {
                    MetricRegistry.this.remove(MetricRegistry.name(childName, name));
                }

                @Override
                public void onMeterAdded(String name, Meter meter) {
                    MetricRegistry.this.register(MetricRegistry.name(childName, name), meter);
                }

                @Override
                public void onMeterRemoved(String name) {
                    MetricRegistry.this.remove(MetricRegistry.name(childName, name));
                }

                @Override
                public void onTimerAdded(String name, Timer timer) {
                    MetricRegistry.this.register(MetricRegistry.name(childName, name), timer);
                }

                @Override
                public void onTimerRemoved(String name) {
                    MetricRegistry.this.remove(MetricRegistry.name(childName, name));
                }
            });
        } else if (metric instanceof MetricSet) {
            this.registerAll(name, (MetricSet)metric);
        } else {
            Metric existing = this.metrics.putIfAbsent(name, metric);
            if (existing == null) {
                this.onMetricAdded(name, metric);
            } else {
                throw new IllegalArgumentException("A metric named " + name + " already exists");
            }
        }
        return metric;
    }

    public void registerAll(MetricSet metrics) throws IllegalArgumentException {
        this.registerAll(null, metrics);
    }

    public Counter counter(String name) {
        return this.getOrAdd(name, MetricBuilder.COUNTERS);
    }

    public Counter counter(String name, final MetricSupplier<Counter> supplier) {
        return this.getOrAdd(name, new MetricBuilder<Counter>(){

            @Override
            public Counter newMetric() {
                return (Counter)supplier.newMetric();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Counter.class.isInstance(metric);
            }
        });
    }

    public Histogram histogram(String name) {
        return this.getOrAdd(name, MetricBuilder.HISTOGRAMS);
    }

    public Histogram histogram(String name, final MetricSupplier<Histogram> supplier) {
        return this.getOrAdd(name, new MetricBuilder<Histogram>(){

            @Override
            public Histogram newMetric() {
                return (Histogram)supplier.newMetric();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Histogram.class.isInstance(metric);
            }
        });
    }

    public Meter meter(String name) {
        return this.getOrAdd(name, MetricBuilder.METERS);
    }

    public Meter meter(String name, final MetricSupplier<Meter> supplier) {
        return this.getOrAdd(name, new MetricBuilder<Meter>(){

            @Override
            public Meter newMetric() {
                return (Meter)supplier.newMetric();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Meter.class.isInstance(metric);
            }
        });
    }

    public Timer timer(String name) {
        return this.getOrAdd(name, MetricBuilder.TIMERS);
    }

    public Timer timer(String name, final MetricSupplier<Timer> supplier) {
        return this.getOrAdd(name, new MetricBuilder<Timer>(){

            @Override
            public Timer newMetric() {
                return (Timer)supplier.newMetric();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Timer.class.isInstance(metric);
            }
        });
    }

    public <T extends Gauge> T gauge(String name) {
        return (T)this.getOrAdd(name, MetricBuilder.GAUGES);
    }

    public <T extends Gauge> T gauge(String name, final MetricSupplier<T> supplier) {
        return (T)((Gauge)this.getOrAdd(name, new MetricBuilder<T>(){

            @Override
            public T newMetric() {
                return (Gauge)supplier.newMetric();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Gauge.class.isInstance(metric);
            }
        }));
    }

    public boolean remove(String name) {
        Metric metric = (Metric)this.metrics.remove(name);
        if (metric != null) {
            this.onMetricRemoved(name, metric);
            return true;
        }
        return false;
    }

    public void removeMatching(MetricFilter filter) {
        for (Map.Entry entry : this.metrics.entrySet()) {
            if (!filter.matches((String)entry.getKey(), (Metric)entry.getValue())) continue;
            this.remove((String)entry.getKey());
        }
    }

    public void addListener(MetricRegistryListener listener) {
        this.listeners.add(listener);
        for (Map.Entry entry : this.metrics.entrySet()) {
            this.notifyListenerOfAddedMetric(listener, (Metric)entry.getValue(), (String)entry.getKey());
        }
    }

    public void removeListener(MetricRegistryListener listener) {
        this.listeners.remove(listener);
    }

    public SortedSet<String> getNames() {
        return Collections.unmodifiableSortedSet(new TreeSet(this.metrics.keySet()));
    }

    public SortedMap<String, Gauge> getGauges() {
        return this.getGauges(MetricFilter.ALL);
    }

    public SortedMap<String, Gauge> getGauges(MetricFilter filter) {
        return this.getMetrics(Gauge.class, filter);
    }

    public SortedMap<String, Counter> getCounters() {
        return this.getCounters(MetricFilter.ALL);
    }

    public SortedMap<String, Counter> getCounters(MetricFilter filter) {
        return this.getMetrics(Counter.class, filter);
    }

    public SortedMap<String, Histogram> getHistograms() {
        return this.getHistograms(MetricFilter.ALL);
    }

    public SortedMap<String, Histogram> getHistograms(MetricFilter filter) {
        return this.getMetrics(Histogram.class, filter);
    }

    public SortedMap<String, Meter> getMeters() {
        return this.getMeters(MetricFilter.ALL);
    }

    public SortedMap<String, Meter> getMeters(MetricFilter filter) {
        return this.getMetrics(Meter.class, filter);
    }

    public SortedMap<String, Timer> getTimers() {
        return this.getTimers(MetricFilter.ALL);
    }

    public SortedMap<String, Timer> getTimers(MetricFilter filter) {
        return this.getMetrics(Timer.class, filter);
    }

    private <T extends Metric> T getOrAdd(String name, MetricBuilder<T> builder) {
        block4: {
            Metric metric = (Metric)this.metrics.get(name);
            if (builder.isInstance(metric)) {
                return (T)metric;
            }
            if (metric == null) {
                try {
                    return this.register(name, builder.newMetric());
                }
                catch (IllegalArgumentException e) {
                    Metric added = (Metric)this.metrics.get(name);
                    if (!builder.isInstance(added)) break block4;
                    return (T)added;
                }
            }
        }
        throw new IllegalArgumentException(name + " is already used for a different type of metric");
    }

    private <T extends Metric> SortedMap<String, T> getMetrics(Class<T> klass, MetricFilter filter) {
        TreeMap<String, Metric> timers = new TreeMap<String, Metric>();
        for (Map.Entry entry : this.metrics.entrySet()) {
            if (!klass.isInstance(entry.getValue()) || !filter.matches((String)entry.getKey(), (Metric)entry.getValue())) continue;
            timers.put((String)entry.getKey(), (Metric)entry.getValue());
        }
        return Collections.unmodifiableSortedMap(timers);
    }

    private void onMetricAdded(String name, Metric metric) {
        for (MetricRegistryListener listener : this.listeners) {
            this.notifyListenerOfAddedMetric(listener, metric, name);
        }
    }

    private void notifyListenerOfAddedMetric(MetricRegistryListener listener, Metric metric, String name) {
        if (metric instanceof Gauge) {
            listener.onGaugeAdded(name, (Gauge)metric);
        } else if (metric instanceof Counter) {
            listener.onCounterAdded(name, (Counter)metric);
        } else if (metric instanceof Histogram) {
            listener.onHistogramAdded(name, (Histogram)metric);
        } else if (metric instanceof Meter) {
            listener.onMeterAdded(name, (Meter)metric);
        } else if (metric instanceof Timer) {
            listener.onTimerAdded(name, (Timer)metric);
        } else {
            throw new IllegalArgumentException("Unknown metric type: " + metric.getClass());
        }
    }

    private void onMetricRemoved(String name, Metric metric) {
        for (MetricRegistryListener listener : this.listeners) {
            this.notifyListenerOfRemovedMetric(name, metric, listener);
        }
    }

    private void notifyListenerOfRemovedMetric(String name, Metric metric, MetricRegistryListener listener) {
        if (metric instanceof Gauge) {
            listener.onGaugeRemoved(name);
        } else if (metric instanceof Counter) {
            listener.onCounterRemoved(name);
        } else if (metric instanceof Histogram) {
            listener.onHistogramRemoved(name);
        } else if (metric instanceof Meter) {
            listener.onMeterRemoved(name);
        } else if (metric instanceof Timer) {
            listener.onTimerRemoved(name);
        } else {
            throw new IllegalArgumentException("Unknown metric type: " + metric.getClass());
        }
    }

    public void registerAll(String prefix, MetricSet metrics) throws IllegalArgumentException {
        for (Map.Entry<String, Metric> entry : metrics.getMetrics().entrySet()) {
            if (entry.getValue() instanceof MetricSet) {
                this.registerAll(MetricRegistry.name(prefix, entry.getKey()), (MetricSet)entry.getValue());
                continue;
            }
            this.register(MetricRegistry.name(prefix, entry.getKey()), entry.getValue());
        }
    }

    @Override
    public Map<String, Metric> getMetrics() {
        return Collections.unmodifiableMap(this.metrics);
    }

    private static interface MetricBuilder<T extends Metric> {
        public static final MetricBuilder<Counter> COUNTERS = new MetricBuilder<Counter>(){

            @Override
            public Counter newMetric() {
                return new Counter();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Counter.class.isInstance(metric);
            }
        };
        public static final MetricBuilder<Histogram> HISTOGRAMS = new MetricBuilder<Histogram>(){

            @Override
            public Histogram newMetric() {
                return new Histogram(new ExponentiallyDecayingReservoir());
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Histogram.class.isInstance(metric);
            }
        };
        public static final MetricBuilder<Meter> METERS = new MetricBuilder<Meter>(){

            @Override
            public Meter newMetric() {
                return new Meter();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Meter.class.isInstance(metric);
            }
        };
        public static final MetricBuilder<Timer> TIMERS = new MetricBuilder<Timer>(){

            @Override
            public Timer newMetric() {
                return new Timer();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Timer.class.isInstance(metric);
            }
        };
        public static final MetricBuilder<Gauge> GAUGES = new MetricBuilder<Gauge>(){

            @Override
            public Gauge newMetric() {
                return new DefaultSettableGauge();
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Gauge.class.isInstance(metric);
            }
        };

        public T newMetric();

        public boolean isInstance(Metric var1);
    }

    @FunctionalInterface
    public static interface MetricSupplier<T extends Metric> {
        public T newMetric();
    }
}

