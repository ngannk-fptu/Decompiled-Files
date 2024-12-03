/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.hazelcast.internal.metrics.impl;

import com.hazelcast.internal.metrics.DiscardableMetricsProvider;
import com.hazelcast.internal.metrics.DoubleGauge;
import com.hazelcast.internal.metrics.DoubleProbeFunction;
import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.internal.metrics.MetricsProvider;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.ProbeBuilder;
import com.hazelcast.internal.metrics.ProbeFunction;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.metrics.impl.DoubleGaugeImpl;
import com.hazelcast.internal.metrics.impl.LockStripe;
import com.hazelcast.internal.metrics.impl.LongGaugeImpl;
import com.hazelcast.internal.metrics.impl.ProbeBuilderImpl;
import com.hazelcast.internal.metrics.impl.ProbeInstance;
import com.hazelcast.internal.metrics.impl.SourceMetadata;
import com.hazelcast.internal.metrics.renderers.ProbeRenderer;
import com.hazelcast.internal.util.concurrent.ThreadFactoryImpl;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.ConcurrentReferenceHashMap;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.ThreadUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

public class MetricsRegistryImpl
implements MetricsRegistry {
    private static final Comparator<ProbeInstance> COMPARATOR = new Comparator<ProbeInstance>(){

        @Override
        public int compare(ProbeInstance o1, ProbeInstance o2) {
            return o1.name.compareTo(o2.name);
        }
    };
    final ILogger logger;
    private final ProbeLevel minimumLevel;
    private final ScheduledExecutorService scheduler;
    private final ConcurrentMap<String, ProbeInstance> probeInstances = new ConcurrentHashMap<String, ProbeInstance>();
    private final ConcurrentMap<Class<?>, SourceMetadata> metadataMap = new ConcurrentReferenceHashMap();
    private final LockStripe lockStripe = new LockStripe();
    private final AtomicLong modCount = new AtomicLong();
    private final AtomicReference<SortedProbeInstances> sortedProbeInstances = new AtomicReference<SortedProbeInstances>(new SortedProbeInstances(0L, Collections.emptyList()));

    public MetricsRegistryImpl(ILogger logger, ProbeLevel minimumLevel) {
        this("default", logger, minimumLevel);
    }

    public MetricsRegistryImpl(String name, ILogger logger, ProbeLevel minimumLevel) {
        this.logger = Preconditions.checkNotNull(logger, "logger can't be null");
        this.minimumLevel = Preconditions.checkNotNull(minimumLevel, "minimumLevel can't be null");
        this.scheduler = new ScheduledThreadPoolExecutor(2, new ThreadFactoryImpl(ThreadUtil.createThreadPoolName(name, "MetricsRegistry")));
        if (logger.isFinestEnabled()) {
            logger.finest("MetricsRegistry minimumLevel:" + (Object)((Object)minimumLevel));
        }
    }

    @Override
    public ProbeLevel minimumLevel() {
        return this.minimumLevel;
    }

    long modCount() {
        return this.modCount.get();
    }

    @Override
    public Set<String> getNames() {
        HashSet names = new HashSet(this.probeInstances.keySet());
        return Collections.unmodifiableSet(names);
    }

    SourceMetadata loadSourceMetadata(Class<?> clazz) {
        SourceMetadata metadata = (SourceMetadata)this.metadataMap.get(clazz);
        if (metadata == null) {
            metadata = new SourceMetadata(clazz);
            SourceMetadata found = this.metadataMap.putIfAbsent(clazz, metadata);
            metadata = found == null ? metadata : found;
        }
        return metadata;
    }

    @Override
    public <S> void scanAndRegister(S source, String namePrefix) {
        Preconditions.checkNotNull(source, "source can't be null");
        Preconditions.checkNotNull(namePrefix, "namePrefix can't be null");
        SourceMetadata metadata = this.loadSourceMetadata(source.getClass());
        metadata.register(this, source, namePrefix);
    }

    @Override
    public <S> void register(S source, String name, ProbeLevel level, LongProbeFunction<S> function) {
        Preconditions.checkNotNull(source, "source can't be null");
        Preconditions.checkNotNull(name, "name can't be null");
        Preconditions.checkNotNull(function, "function can't be null");
        Preconditions.checkNotNull(level, "level can't be null");
        this.registerInternal(source, name, level, function);
    }

    @Override
    public <S> void register(S source, String name, ProbeLevel level, DoubleProbeFunction<S> function) {
        Preconditions.checkNotNull(source, "source can't be null");
        Preconditions.checkNotNull(name, "name can't be null");
        Preconditions.checkNotNull(function, "function can't be null");
        Preconditions.checkNotNull(level, "level can't be null");
        this.registerInternal(source, name, level, function);
    }

    ProbeInstance getProbeInstance(String name) {
        Preconditions.checkNotNull(name, "name can't be null");
        return (ProbeInstance)this.probeInstances.get(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    <S> void registerInternal(S source, String name, ProbeLevel probeLevel, ProbeFunction function) {
        if (!probeLevel.isEnabled(this.minimumLevel)) {
            return;
        }
        ProbeInstance<S> probeInstance = this.probeInstances.putIfAbsent(name, new ProbeInstance<S>(name, source, function));
        if (probeInstance != null) {
            this.logOverwrite(probeInstance);
            Object object = this.lockStripe.getLock(source);
            synchronized (object) {
                probeInstance.source = source;
                probeInstance.function = function;
            }
        }
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Registered probeInstance " + name);
        }
        this.modCount.incrementAndGet();
    }

    private void logOverwrite(ProbeInstance probeInstance) {
        if (probeInstance.function != null || probeInstance.source != null) {
            this.logger.warning(String.format("Overwriting existing probe '%s'", probeInstance.name));
        }
    }

    @Override
    public LongGaugeImpl newLongGauge(String name) {
        Preconditions.checkNotNull(name, "name can't be null");
        return new LongGaugeImpl(this, name);
    }

    @Override
    public DoubleGauge newDoubleGauge(String name) {
        Preconditions.checkNotNull(name, "name can't be null");
        return new DoubleGaugeImpl(this, name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <S> void deregister(S source) {
        if (source == null) {
            return;
        }
        boolean changed = false;
        for (Map.Entry entry : this.probeInstances.entrySet()) {
            ProbeInstance probeInstance = (ProbeInstance)entry.getValue();
            if (probeInstance.source != source) continue;
            String name = (String)entry.getKey();
            boolean destroyed = false;
            Object object = this.lockStripe.getLock(source);
            synchronized (object) {
                if (probeInstance.source == source) {
                    changed = true;
                    this.probeInstances.remove(name);
                    probeInstance.source = null;
                    probeInstance.function = null;
                    destroyed = true;
                }
            }
            if (!destroyed || !this.logger.isFinestEnabled()) continue;
            this.logger.finest("Destroying probeInstance " + name);
        }
        if (changed) {
            this.modCount.incrementAndGet();
        }
    }

    @Override
    public void render(ProbeRenderer renderer) {
        Preconditions.checkNotNull(renderer, "renderer can't be null");
        for (ProbeInstance probeInstance : this.getSortedProbeInstances()) {
            this.render(renderer, probeInstance);
        }
    }

    @Override
    public void collectMetrics(Object ... objects) {
        for (Object object : objects) {
            if (!(object instanceof MetricsProvider)) continue;
            ((MetricsProvider)object).provideMetrics(this);
        }
    }

    @Override
    public void discardMetrics(Object ... objects) {
        for (Object object : objects) {
            if (!(object instanceof DiscardableMetricsProvider)) continue;
            ((DiscardableMetricsProvider)object).discardMetrics(this);
        }
    }

    List<ProbeInstance> getSortedProbeInstances() {
        SortedProbeInstances sortedInstances;
        long modCountLocal = this.modCount.get();
        SortedProbeInstances sortedInstancesOld = this.sortedProbeInstances.get();
        if (sortedInstancesOld.mod < modCountLocal) {
            ArrayList<ProbeInstance> sorted = new ArrayList<ProbeInstance>(this.probeInstances.values());
            Collections.sort(sorted, COMPARATOR);
            sortedInstances = new SortedProbeInstances(modCountLocal, sorted);
            this.sortedProbeInstances.compareAndSet(sortedInstancesOld, sortedInstances);
        } else {
            sortedInstances = sortedInstancesOld;
        }
        return sortedInstances.probeInstances;
    }

    private void render(ProbeRenderer renderer, ProbeInstance probeInstance) {
        ProbeFunction function = probeInstance.function;
        Object source = probeInstance.source;
        String name = probeInstance.name;
        if (function == null || source == null) {
            renderer.renderNoValue(name);
            return;
        }
        try {
            if (function instanceof LongProbeFunction) {
                LongProbeFunction longFunction = (LongProbeFunction)function;
                renderer.renderLong(name, longFunction.get(source));
            } else {
                DoubleProbeFunction doubleFunction = (DoubleProbeFunction)function;
                renderer.renderDouble(name, doubleFunction.get(source));
            }
        }
        catch (Exception e) {
            renderer.renderException(name, e);
        }
    }

    @Override
    public void scheduleAtFixedRate(Runnable publisher, long period, TimeUnit timeUnit, ProbeLevel probeLevel) {
        if (!probeLevel.isEnabled(this.minimumLevel)) {
            return;
        }
        this.scheduler.scheduleAtFixedRate(publisher, 0L, period, timeUnit);
    }

    public void shutdown() {
        this.scheduler.shutdownNow();
    }

    @Override
    public ProbeBuilder newProbeBuilder() {
        return new ProbeBuilderImpl(this);
    }

    private static class SortedProbeInstances {
        private final long mod;
        private final List<ProbeInstance> probeInstances;

        SortedProbeInstances(long mod, @Nonnull List<ProbeInstance> probeInstances) {
            this.mod = mod;
            this.probeInstances = probeInstances;
        }
    }
}

