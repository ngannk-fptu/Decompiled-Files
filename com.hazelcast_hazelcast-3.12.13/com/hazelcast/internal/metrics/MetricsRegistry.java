/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics;

import com.hazelcast.internal.metrics.DoubleGauge;
import com.hazelcast.internal.metrics.DoubleProbeFunction;
import com.hazelcast.internal.metrics.LongGauge;
import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.internal.metrics.ProbeBuilder;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.metrics.renderers.ProbeRenderer;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface MetricsRegistry {
    public ProbeLevel minimumLevel();

    public LongGauge newLongGauge(String var1);

    public DoubleGauge newDoubleGauge(String var1);

    public Set<String> getNames();

    public <S> void scanAndRegister(S var1, String var2);

    public <S> void register(S var1, String var2, ProbeLevel var3, LongProbeFunction<S> var4);

    public <S> void register(S var1, String var2, ProbeLevel var3, DoubleProbeFunction<S> var4);

    public <S> void deregister(S var1);

    public void scheduleAtFixedRate(Runnable var1, long var2, TimeUnit var4, ProbeLevel var5);

    public void render(ProbeRenderer var1);

    public void collectMetrics(Object ... var1);

    public void discardMetrics(Object ... var1);

    public ProbeBuilder newProbeBuilder();
}

