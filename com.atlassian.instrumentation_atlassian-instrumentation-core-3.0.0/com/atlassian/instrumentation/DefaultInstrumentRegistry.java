/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation;

import com.atlassian.instrumentation.AbsoluteAtomicCounter;
import com.atlassian.instrumentation.AbsoluteCounter;
import com.atlassian.instrumentation.AtomicCounter;
import com.atlassian.instrumentation.AtomicGauge;
import com.atlassian.instrumentation.Counter;
import com.atlassian.instrumentation.DefaultRegistryConfiguration;
import com.atlassian.instrumentation.DerivedAtomicCounter;
import com.atlassian.instrumentation.DerivedCounter;
import com.atlassian.instrumentation.Gauge;
import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.InstrumentRegistry;
import com.atlassian.instrumentation.RegistryConfiguration;
import com.atlassian.instrumentation.caches.CacheCollector;
import com.atlassian.instrumentation.caches.CacheCounter;
import com.atlassian.instrumentation.operations.OpCounter;
import com.atlassian.instrumentation.operations.OpSnapshot;
import com.atlassian.instrumentation.operations.OpTimer;
import com.atlassian.instrumentation.operations.OpTimerFactory;
import com.atlassian.instrumentation.operations.SimpleOpTimerFactory;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultInstrumentRegistry
implements InstrumentRegistry {
    private static final SimpleOpTimerFactory SIMPLE_OPTIMER_FACTORY = new SimpleOpTimerFactory();
    private final ConcurrentHashMap<String, Instrument> mapOfInstruments = new ConcurrentHashMap();
    private final OpTimerFactory opTimerFactory;
    private final RegistryConfiguration registryConfiguration;

    public DefaultInstrumentRegistry() {
        this(SIMPLE_OPTIMER_FACTORY, new DefaultRegistryConfiguration());
    }

    public DefaultInstrumentRegistry(OpTimerFactory opTimerFactory, RegistryConfiguration registryConfiguration) {
        this.registryConfiguration = Assertions.notNull("registryConfiguration", registryConfiguration);
        this.opTimerFactory = Assertions.notNull("opTimerFactory", opTimerFactory);
    }

    @Override
    public RegistryConfiguration getRegistryConfiguration() {
        return this.registryConfiguration;
    }

    @Override
    public Instrument putInstrument(Instrument instrument) {
        Assertions.notNull("instrument", instrument);
        return this.mapOfInstruments.put(instrument.getName(), instrument);
    }

    @Override
    public Instrument getInstrument(String name) {
        Assertions.notNull("name", name);
        return this.mapOfInstruments.get(name);
    }

    private void checkInstrumentType(Instrument instrument, Class instrumentClass) {
        Assertions.notNull("instrumentClass", instrumentClass);
        if (instrument != null && !instrumentClass.isAssignableFrom(instrument.getClass())) {
            throw new NamedInstrumentException(instrument.getName());
        }
    }

    protected Instrument putIfAbsent(String name, Instrument possiblyNeeded) {
        return this.mapOfInstruments.putIfAbsent(name, possiblyNeeded);
    }

    @Override
    public AbsoluteCounter pullAbsoluteCounter(String name) {
        Instrument instrument = this.getInstrument(name);
        if (instrument == null) {
            AbsoluteAtomicCounter possiblyNeeded = new AbsoluteAtomicCounter(name);
            instrument = this.putIfAbsent(name, possiblyNeeded);
            instrument = instrument != null ? instrument : possiblyNeeded;
        }
        this.checkInstrumentType(instrument, AbsoluteCounter.class);
        return (AbsoluteCounter)instrument;
    }

    @Override
    public Counter pullCounter(String name) {
        Instrument instrument = this.getInstrument(name);
        if (instrument == null) {
            AtomicCounter possiblyNeeded = new AtomicCounter(name);
            instrument = this.putIfAbsent(name, possiblyNeeded);
            instrument = instrument != null ? instrument : possiblyNeeded;
        }
        this.checkInstrumentType(instrument, Counter.class);
        return (Counter)instrument;
    }

    @Override
    public DerivedCounter pullDerivedCounter(String name) {
        Instrument instrument = this.getInstrument(name);
        if (instrument == null) {
            DerivedAtomicCounter possiblyNeeded = new DerivedAtomicCounter(name);
            instrument = this.putIfAbsent(name, possiblyNeeded);
            instrument = instrument != null ? instrument : possiblyNeeded;
        }
        this.checkInstrumentType(instrument, DerivedCounter.class);
        return (DerivedCounter)instrument;
    }

    @Override
    public Gauge pullGauge(String name) {
        Instrument instrument = this.getInstrument(name);
        if (instrument == null) {
            AtomicGauge possiblyNeeded = new AtomicGauge(name);
            instrument = this.putIfAbsent(name, possiblyNeeded);
            instrument = instrument != null ? instrument : possiblyNeeded;
        }
        this.checkInstrumentType(instrument, Gauge.class);
        return (Gauge)instrument;
    }

    @Override
    public CacheCollector pullCacheCollector(String name, CacheCollector.Sizer sizer) {
        Instrument instrument = this.getInstrument(name);
        if (instrument == null) {
            CacheCollector possiblyNeeded = new CacheCollector(name, sizer);
            instrument = this.putIfAbsent(name, possiblyNeeded);
            instrument = instrument != null ? instrument : possiblyNeeded;
        }
        this.checkInstrumentType(instrument, CacheCollector.class);
        return (CacheCollector)instrument;
    }

    @Override
    public CacheCollector pullCacheCollector(String name) {
        return this.pullCacheCollector(name, CacheCollector.NOOP_SIZER);
    }

    @Override
    public CacheCounter pullCacheCounter(String name, CacheCounter.Sizer sizer) {
        Instrument instrument = this.getInstrument(name);
        if (instrument == null) {
            CacheCounter possiblyNeeded = new CacheCounter(name, sizer);
            instrument = this.putIfAbsent(name, possiblyNeeded);
            instrument = instrument != null ? instrument : possiblyNeeded;
        }
        this.checkInstrumentType(instrument, CacheCounter.class);
        return (CacheCounter)instrument;
    }

    @Override
    public CacheCounter pullCacheCounter(String name) {
        return this.pullCacheCounter(name, CacheCounter.NOOP_SIZER);
    }

    @Override
    public OpCounter pullOpCounter(String name) {
        Instrument instrument = this.getInstrument(name);
        if (instrument == null) {
            OpCounter possiblyNeeded = new OpCounter(name);
            instrument = this.putIfAbsent(name, possiblyNeeded);
            instrument = instrument != null ? instrument : possiblyNeeded;
        }
        this.checkInstrumentType(instrument, OpCounter.class);
        return (OpCounter)instrument;
    }

    @Override
    public OpTimer pullTimer(String name) {
        OpCounter opCounter = this.pullOpCounter(name);
        return this.opTimerFactory.createOpTimer(name, this.registryConfiguration.isCPUCostCollected(), new AddToOpCounterCallback(opCounter));
    }

    @Override
    public List<Instrument> snapshotInstruments() {
        return new ArrayList<Instrument>(this.mapOfInstruments.values());
    }

    @Override
    public int getNumberOfInstruments() {
        return this.mapOfInstruments.size();
    }

    private static class AddToOpCounterCallback
    implements OpTimer.OnEndCallback {
        private final OpCounter opCounter;

        public AddToOpCounterCallback(OpCounter opCounter) {
            this.opCounter = opCounter;
        }

        @Override
        public void onEndCalled(OpSnapshot opSnapshot) {
            this.opCounter.add(opSnapshot);
        }
    }

    private static class NamedInstrumentException
    extends IllegalArgumentException {
        private NamedInstrumentException(String name) {
            super("An instrument of a different type with the name : '" + name + "already exists");
        }
    }
}

