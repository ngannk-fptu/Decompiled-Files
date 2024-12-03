/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.MetricRegistry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class SharedMetricRegistries {
    private static final ConcurrentMap<String, MetricRegistry> REGISTRIES = new ConcurrentHashMap<String, MetricRegistry>();
    private static AtomicReference<String> defaultRegistryName = new AtomicReference();

    static void setDefaultRegistryName(AtomicReference<String> defaultRegistryName) {
        SharedMetricRegistries.defaultRegistryName = defaultRegistryName;
    }

    private SharedMetricRegistries() {
    }

    public static void clear() {
        REGISTRIES.clear();
    }

    public static Set<String> names() {
        return REGISTRIES.keySet();
    }

    public static void remove(String key) {
        REGISTRIES.remove(key);
    }

    public static MetricRegistry add(String name, MetricRegistry registry) {
        return REGISTRIES.putIfAbsent(name, registry);
    }

    public static MetricRegistry getOrCreate(String name) {
        MetricRegistry existing = (MetricRegistry)REGISTRIES.get(name);
        if (existing == null) {
            MetricRegistry created = new MetricRegistry();
            MetricRegistry raced = SharedMetricRegistries.add(name, created);
            if (raced == null) {
                return created;
            }
            return raced;
        }
        return existing;
    }

    public static synchronized MetricRegistry setDefault(String name) {
        MetricRegistry registry = SharedMetricRegistries.getOrCreate(name);
        return SharedMetricRegistries.setDefault(name, registry);
    }

    public static MetricRegistry setDefault(String name, MetricRegistry metricRegistry) {
        if (defaultRegistryName.compareAndSet(null, name)) {
            SharedMetricRegistries.add(name, metricRegistry);
            return metricRegistry;
        }
        throw new IllegalStateException("Default metric registry name is already set.");
    }

    public static MetricRegistry getDefault() {
        MetricRegistry metricRegistry = SharedMetricRegistries.tryGetDefault();
        if (metricRegistry == null) {
            throw new IllegalStateException("Default registry name has not been set.");
        }
        return metricRegistry;
    }

    public static MetricRegistry tryGetDefault() {
        String name = defaultRegistryName.get();
        if (name != null) {
            return SharedMetricRegistries.getOrCreate(name);
        }
        return null;
    }
}

