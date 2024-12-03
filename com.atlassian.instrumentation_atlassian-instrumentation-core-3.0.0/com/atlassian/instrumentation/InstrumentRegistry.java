/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation;

import com.atlassian.instrumentation.AbsoluteCounter;
import com.atlassian.instrumentation.Counter;
import com.atlassian.instrumentation.DerivedCounter;
import com.atlassian.instrumentation.Gauge;
import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.RegistryConfiguration;
import com.atlassian.instrumentation.caches.CacheCollector;
import com.atlassian.instrumentation.caches.CacheCounter;
import com.atlassian.instrumentation.operations.OpCounter;
import com.atlassian.instrumentation.operations.OpTimer;
import java.util.List;

public interface InstrumentRegistry {
    public RegistryConfiguration getRegistryConfiguration();

    public Instrument putInstrument(Instrument var1);

    public Instrument getInstrument(String var1);

    public AbsoluteCounter pullAbsoluteCounter(String var1);

    public Counter pullCounter(String var1);

    public DerivedCounter pullDerivedCounter(String var1);

    public Gauge pullGauge(String var1);

    public CacheCollector pullCacheCollector(String var1, CacheCollector.Sizer var2);

    public CacheCollector pullCacheCollector(String var1);

    public CacheCounter pullCacheCounter(String var1, CacheCounter.Sizer var2);

    public CacheCounter pullCacheCounter(String var1);

    public OpCounter pullOpCounter(String var1);

    public OpTimer pullTimer(String var1);

    public List<Instrument> snapshotInstruments();

    public int getNumberOfInstruments();
}

