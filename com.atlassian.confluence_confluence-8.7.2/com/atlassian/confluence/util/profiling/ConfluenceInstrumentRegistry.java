/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.instrumentation.AbsoluteAtomicCounter
 *  com.atlassian.instrumentation.AbsoluteCounter
 *  com.atlassian.instrumentation.AtomicCounter
 *  com.atlassian.instrumentation.AtomicGauge
 *  com.atlassian.instrumentation.Counter
 *  com.atlassian.instrumentation.DerivedAtomicCounter
 *  com.atlassian.instrumentation.DerivedCounter
 *  com.atlassian.instrumentation.Gauge
 *  com.atlassian.instrumentation.Instrument
 *  com.atlassian.instrumentation.RegistryConfiguration
 *  com.atlassian.instrumentation.caches.CacheCollector
 *  com.atlassian.instrumentation.caches.CacheCollector$Sizer
 *  com.atlassian.instrumentation.caches.CacheCounter
 *  com.atlassian.instrumentation.caches.CacheCounter$Sizer
 *  com.atlassian.instrumentation.operations.OpCounter
 *  com.atlassian.instrumentation.operations.OpTimer
 *  com.atlassian.instrumentation.operations.OpTimerFactory
 *  com.atlassian.instrumentation.operations.SimpleOpTimerFactory
 *  com.atlassian.instrumentation.utils.dbc.Assertions
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.confluence.util.profiling.ControllableInstrumentRegistry;
import com.atlassian.instrumentation.AbsoluteAtomicCounter;
import com.atlassian.instrumentation.AbsoluteCounter;
import com.atlassian.instrumentation.AtomicCounter;
import com.atlassian.instrumentation.AtomicGauge;
import com.atlassian.instrumentation.Counter;
import com.atlassian.instrumentation.DerivedAtomicCounter;
import com.atlassian.instrumentation.DerivedCounter;
import com.atlassian.instrumentation.Gauge;
import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.RegistryConfiguration;
import com.atlassian.instrumentation.caches.CacheCollector;
import com.atlassian.instrumentation.caches.CacheCounter;
import com.atlassian.instrumentation.operations.OpCounter;
import com.atlassian.instrumentation.operations.OpTimer;
import com.atlassian.instrumentation.operations.OpTimerFactory;
import com.atlassian.instrumentation.operations.SimpleOpTimerFactory;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ConfluenceInstrumentRegistry
implements ControllableInstrumentRegistry,
ApplicationContextAware {
    private static final SimpleOpTimerFactory SIMPLE_OPTIMER_FACTORY = new SimpleOpTimerFactory();
    private ApplicationContext applicationContext;
    private boolean isEnable;
    private Map<String, Instrument> mapOfInstruments = new ConcurrentHashMap<String, Instrument>();
    private final OpTimerFactory opTimerFactory;
    private Supplier<RegistryConfiguration> registryConfigurationSupplier;

    public ConfluenceInstrumentRegistry(OpTimerFactory opTimerFactory) {
        this(opTimerFactory, null);
    }

    public ConfluenceInstrumentRegistry(OpTimerFactory opTimerFactory, Supplier<RegistryConfiguration> registryConfigurationSupplier) {
        this.opTimerFactory = (OpTimerFactory)Assertions.notNull((String)"opTimerFactory", (Object)opTimerFactory);
        this.registryConfigurationSupplier = registryConfigurationSupplier != null ? registryConfigurationSupplier : () -> (RegistryConfiguration)this.applicationContext.getBean("confluenceInstrumentRegistryConfiguration", RegistryConfiguration.class);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public RegistryConfiguration getRegistryConfiguration() {
        return this.registryConfigurationSupplier.get();
    }

    public Instrument putInstrument(Instrument instrument) {
        Assertions.notNull((String)"instrument", (Object)instrument);
        return this.mapOfInstruments.put(instrument.getName(), instrument);
    }

    public Instrument getInstrument(String name) {
        Assertions.notNull((String)"name", (Object)name);
        return this.mapOfInstruments.get(name);
    }

    private void checkInstrumentType(Instrument instrument, Class instrumentClass) {
        Assertions.notNull((String)"instrumentClass", (Object)instrumentClass);
        if (instrument != null && !instrumentClass.isAssignableFrom(instrument.getClass())) {
            throw new NamedInstrumentException(instrument.getName());
        }
    }

    protected Instrument putIfAbsent(String name, Instrument possiblyNeeded) {
        return this.mapOfInstruments.putIfAbsent(name, possiblyNeeded);
    }

    public AbsoluteCounter pullAbsoluteCounter(String name) {
        Instrument instrument = this.getInstrument(name);
        if (instrument == null) {
            AbsoluteAtomicCounter possiblyNeeded = new AbsoluteAtomicCounter(name);
            Instrument instrument1 = this.putIfAbsent(name, (Instrument)possiblyNeeded);
            instrument = instrument1 != null ? instrument1 : possiblyNeeded;
        }
        this.checkInstrumentType(instrument, AbsoluteCounter.class);
        return (AbsoluteCounter)instrument;
    }

    public Counter pullCounter(String name) {
        Instrument instrument = this.getInstrument(name);
        if (instrument == null) {
            AtomicCounter possiblyNeeded = new AtomicCounter(name);
            Instrument instrument1 = this.putIfAbsent(name, (Instrument)possiblyNeeded);
            instrument = instrument1 != null ? instrument1 : possiblyNeeded;
        }
        this.checkInstrumentType(instrument, Counter.class);
        return (Counter)instrument;
    }

    public DerivedCounter pullDerivedCounter(String name) {
        Instrument instrument = this.getInstrument(name);
        if (instrument == null) {
            DerivedAtomicCounter possiblyNeeded = new DerivedAtomicCounter(name);
            Instrument instrument1 = this.putIfAbsent(name, (Instrument)possiblyNeeded);
            instrument = instrument1 != null ? instrument1 : possiblyNeeded;
        }
        this.checkInstrumentType(instrument, DerivedCounter.class);
        return (DerivedCounter)instrument;
    }

    public Gauge pullGauge(String name) {
        Instrument instrument = this.getInstrument(name);
        if (instrument == null) {
            AtomicGauge possiblyNeeded = new AtomicGauge(name);
            Instrument instrument1 = this.putIfAbsent(name, (Instrument)possiblyNeeded);
            instrument = instrument1 != null ? instrument1 : possiblyNeeded;
        }
        this.checkInstrumentType(instrument, Gauge.class);
        return (Gauge)instrument;
    }

    public CacheCollector pullCacheCollector(String name, CacheCollector.Sizer sizer) {
        Instrument instrument = this.getInstrument(name);
        if (instrument == null) {
            CacheCollector possiblyNeeded = new CacheCollector(name, sizer);
            Instrument instrument1 = this.putIfAbsent(name, (Instrument)possiblyNeeded);
            instrument = instrument1 != null ? instrument1 : possiblyNeeded;
        }
        this.checkInstrumentType(instrument, CacheCollector.class);
        return (CacheCollector)instrument;
    }

    public CacheCollector pullCacheCollector(String name) {
        return this.pullCacheCollector(name, CacheCollector.NOOP_SIZER);
    }

    public CacheCounter pullCacheCounter(String name, CacheCounter.Sizer sizer) {
        Instrument instrument = this.getInstrument(name);
        if (instrument == null) {
            CacheCounter possiblyNeeded = new CacheCounter(name, sizer);
            Instrument instrument1 = this.putIfAbsent(name, (Instrument)possiblyNeeded);
            instrument = instrument1 != null ? instrument1 : possiblyNeeded;
        }
        this.checkInstrumentType(instrument, CacheCounter.class);
        return (CacheCounter)instrument;
    }

    public CacheCounter pullCacheCounter(String name) {
        return this.pullCacheCounter(name, CacheCounter.NOOP_SIZER);
    }

    public OpCounter pullOpCounter(String name) {
        Instrument instrument = this.getInstrument(name);
        if (instrument == null) {
            OpCounter possiblyNeeded = new OpCounter(name);
            Instrument instrument1 = this.putIfAbsent(name, (Instrument)possiblyNeeded);
            instrument = instrument1 != null ? instrument1 : possiblyNeeded;
        }
        this.checkInstrumentType(instrument, OpCounter.class);
        return (OpCounter)instrument;
    }

    public OpTimer pullTimer(String name) {
        RegistryConfiguration threadLocalInstrumentConfiguration = this.getRegistryConfiguration();
        OpCounter opCounter = this.pullOpCounter(name);
        return this.opTimerFactory.createOpTimer(name, threadLocalInstrumentConfiguration.isCPUCostCollected(), opSnapshot -> opCounter.add(opSnapshot));
    }

    public List<Instrument> snapshotInstruments() {
        if (!this.isEnable) {
            return new ArrayList<Instrument>();
        }
        return new ArrayList<Instrument>(this.mapOfInstruments.values());
    }

    public int getNumberOfInstruments() {
        if (!this.isEnable) {
            return 0;
        }
        return this.mapOfInstruments.size();
    }

    @Override
    public boolean isMonitoringEnabled() {
        return this.isEnable;
    }

    @Override
    public void enableMonitoring() {
        this.isEnable = true;
    }

    @Override
    public void disableMonitoring() {
        this.isEnable = false;
    }

    @Override
    public void clear() {
        this.mapOfInstruments.clear();
    }

    private static class NamedInstrumentException
    extends IllegalArgumentException {
        private NamedInstrumentException(String name) {
            super("An instrument of a different type with the name '" + name + "' already exists");
        }
    }
}

