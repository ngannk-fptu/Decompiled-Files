/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.hazelcast.internal.metrics;

import com.hazelcast.internal.metrics.DoubleProbeFunction;
import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.metrics.ProbeUnit;
import javax.annotation.Nonnull;

public interface ProbeBuilder {
    public ProbeBuilder withTag(String var1, String var2);

    public <S> void register(@Nonnull S var1, @Nonnull String var2, @Nonnull ProbeLevel var3, @Nonnull ProbeUnit var4, @Nonnull DoubleProbeFunction<S> var5);

    public <S> void register(@Nonnull S var1, @Nonnull String var2, @Nonnull ProbeLevel var3, @Nonnull ProbeUnit var4, @Nonnull LongProbeFunction<S> var5);

    public <S> void scanAndRegister(S var1);

    public String metricName();
}

