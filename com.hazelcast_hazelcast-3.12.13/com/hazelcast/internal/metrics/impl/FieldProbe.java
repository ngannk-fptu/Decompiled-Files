/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics.impl;

import com.hazelcast.internal.metrics.DoubleProbeFunction;
import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeFunction;
import com.hazelcast.internal.metrics.impl.MetricsRegistryImpl;
import com.hazelcast.internal.metrics.impl.ProbeBuilderImpl;
import com.hazelcast.internal.metrics.impl.ProbeUtils;
import com.hazelcast.internal.util.counters.Counter;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Semaphore;

abstract class FieldProbe
implements ProbeFunction {
    final Probe probe;
    final Field field;
    final int type;

    FieldProbe(Field field, Probe probe, int type) {
        this.field = field;
        this.probe = probe;
        this.type = type;
        field.setAccessible(true);
    }

    void register(MetricsRegistryImpl metricsRegistry, Object source, String namePrefix) {
        String name = namePrefix + '.' + this.getProbeOrFieldName();
        metricsRegistry.registerInternal(source, name, this.probe.level(), this);
    }

    void register(ProbeBuilderImpl builder, Object source) {
        builder.withTag("unit", this.probe.unit().name().toLowerCase()).register(source, this.getProbeOrFieldName(), this.probe.level(), this);
    }

    private String getProbeOrFieldName() {
        return this.probe.name().length() != 0 ? this.probe.name() : this.field.getName();
    }

    static <S> FieldProbe createFieldProbe(Field field, Probe probe) {
        int type = ProbeUtils.getType(field.getType());
        if (type == -1) {
            throw new IllegalArgumentException(String.format("@Probe field '%s' is of an unhandled type", field));
        }
        if (ProbeUtils.isDouble(type)) {
            return new DoubleFieldProbe(field, probe, type);
        }
        return new LongFieldProbe(field, probe, type);
    }

    static class DoubleFieldProbe<S>
    extends FieldProbe
    implements DoubleProbeFunction<S> {
        public DoubleFieldProbe(Field field, Probe probe, int type) {
            super(field, probe, type);
        }

        @Override
        public double get(S source) throws Exception {
            switch (this.type) {
                case 3: {
                    return this.field.getDouble(source);
                }
                case 4: {
                    Number doubleNumber = (Number)this.field.get(source);
                    return doubleNumber == null ? 0.0 : doubleNumber.doubleValue();
                }
            }
            throw new IllegalStateException("Unhandled type:" + this.type);
        }
    }

    static class LongFieldProbe<S>
    extends FieldProbe
    implements LongProbeFunction<S> {
        public LongFieldProbe(Field field, Probe probe, int type) {
            super(field, probe, type);
        }

        @Override
        public long get(S source) throws Exception {
            switch (this.type) {
                case 1: {
                    return this.field.getLong(source);
                }
                case 2: {
                    Number longNumber = (Number)this.field.get(source);
                    return longNumber == null ? 0L : longNumber.longValue();
                }
                case 6: {
                    Map map = (Map)this.field.get(source);
                    return map == null ? 0L : (long)map.size();
                }
                case 5: {
                    Collection collection = (Collection)this.field.get(source);
                    return collection == null ? 0L : (long)collection.size();
                }
                case 7: {
                    Counter counter = (Counter)this.field.get(source);
                    return counter == null ? 0L : counter.get();
                }
                case 8: {
                    Semaphore semaphore = (Semaphore)this.field.get(source);
                    return semaphore == null ? 0L : (long)semaphore.availablePermits();
                }
            }
            throw new IllegalStateException("Unhandled type:" + this.type);
        }
    }
}

