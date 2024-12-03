/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckReturnValue
 *  javax.annotation.Nonnull
 */
package com.hazelcast.internal.metrics.impl;

import com.hazelcast.internal.metrics.DoubleProbeFunction;
import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.internal.metrics.MetricsUtil;
import com.hazelcast.internal.metrics.ProbeBuilder;
import com.hazelcast.internal.metrics.ProbeFunction;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.metrics.ProbeUnit;
import com.hazelcast.internal.metrics.impl.FieldProbe;
import com.hazelcast.internal.metrics.impl.MethodProbe;
import com.hazelcast.internal.metrics.impl.MetricsRegistryImpl;
import com.hazelcast.internal.metrics.impl.SourceMetadata;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public class ProbeBuilderImpl
implements ProbeBuilder {
    private final MetricsRegistryImpl metricsRegistry;
    private final String keyPrefix;

    ProbeBuilderImpl(MetricsRegistryImpl metricsRegistry) {
        this.metricsRegistry = metricsRegistry;
        this.keyPrefix = "[";
    }

    private ProbeBuilderImpl(MetricsRegistryImpl metricsRegistry, String keyPrefix) {
        this.metricsRegistry = metricsRegistry;
        this.keyPrefix = keyPrefix;
    }

    @Override
    @CheckReturnValue
    public ProbeBuilderImpl withTag(String tag, String value) {
        assert (MetricsUtil.containsSpecialCharacters(tag)) : "tag contains special characters";
        return new ProbeBuilderImpl(this.metricsRegistry, this.keyPrefix + (this.keyPrefix.length() == 1 ? "" : ",") + tag + '=' + MetricsUtil.escapeMetricNamePart(value));
    }

    @Override
    public String metricName() {
        return this.keyPrefix + ']';
    }

    @Override
    public <S> void register(@Nonnull S source, @Nonnull String metricName, @Nonnull ProbeLevel level, @Nonnull ProbeUnit unit, @Nonnull DoubleProbeFunction<S> probe) {
        String name = this.withTag("unit", unit.name().toLowerCase()).withTag("metric", metricName).metricName();
        this.metricsRegistry.register(source, name, level, probe);
    }

    @Override
    public <S> void register(@Nonnull S source, @Nonnull String metricName, @Nonnull ProbeLevel level, @Nonnull ProbeUnit unit, @Nonnull LongProbeFunction<S> probe) {
        String name = this.withTag("unit", unit.name().toLowerCase()).withTag("metric", metricName).metricName();
        this.metricsRegistry.register(source, name, level, probe);
    }

    <S> void register(S source, String metricName, ProbeLevel level, ProbeFunction probe) {
        this.metricsRegistry.registerInternal(source, this.withTag("metric", metricName).metricName(), level, probe);
    }

    @Override
    public <S> void scanAndRegister(S source) {
        SourceMetadata metadata = this.metricsRegistry.loadSourceMetadata(source.getClass());
        for (FieldProbe field : metadata.fields()) {
            field.register(this, source);
        }
        for (MethodProbe method : metadata.methods()) {
            method.register(this, source);
        }
    }
}

