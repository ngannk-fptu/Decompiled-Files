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
import com.hazelcast.util.StringUtil;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Semaphore;

abstract class MethodProbe
implements ProbeFunction {
    private static final Object[] EMPTY_ARGS = new Object[0];
    final Method method;
    final Probe probe;
    final int type;

    MethodProbe(Method method, Probe probe, int type) {
        this.method = method;
        this.probe = probe;
        this.type = type;
        method.setAccessible(true);
    }

    void register(MetricsRegistryImpl metricsRegistry, Object source, String namePrefix) {
        String name = namePrefix + '.' + this.getProbeOrMethodName();
        metricsRegistry.registerInternal(source, name, this.probe.level(), this);
    }

    void register(ProbeBuilderImpl builder, Object source) {
        builder.withTag("unit", this.probe.unit().name().toLowerCase()).register(source, this.getProbeOrMethodName(), this.probe.level(), this);
    }

    private String getProbeOrMethodName() {
        return this.probe.name().length() != 0 ? this.probe.name() : StringUtil.getterIntoProperty(this.method.getName());
    }

    static <S> MethodProbe createMethodProbe(Method method, Probe probe) {
        int type = ProbeUtils.getType(method.getReturnType());
        if (type == -1) {
            throw new IllegalArgumentException(String.format("@Probe method '%s.%s() has an unsupported return type'", method.getDeclaringClass().getName(), method.getName()));
        }
        if (method.getParameterTypes().length != 0) {
            throw new IllegalArgumentException(String.format("@Probe method '%s.%s' can't have arguments", method.getDeclaringClass().getName(), method.getName()));
        }
        if (ProbeUtils.isDouble(type)) {
            return new DoubleMethodProbe(method, probe, type);
        }
        return new LongMethodProbe(method, probe, type);
    }

    static class DoubleMethodProbe<S>
    extends MethodProbe
    implements DoubleProbeFunction<S> {
        public DoubleMethodProbe(Method method, Probe probe, int type) {
            super(method, probe, type);
        }

        @Override
        public double get(S source) throws Exception {
            switch (this.type) {
                case 3: 
                case 4: {
                    Number result = (Number)this.method.invoke(source, EMPTY_ARGS);
                    return result == null ? 0.0 : result.doubleValue();
                }
            }
            throw new IllegalStateException("Unrecognized type:" + this.type);
        }
    }

    static class LongMethodProbe<S>
    extends MethodProbe
    implements LongProbeFunction<S> {
        public LongMethodProbe(Method method, Probe probe, int type) {
            super(method, probe, type);
        }

        @Override
        public long get(S source) throws Exception {
            switch (this.type) {
                case 1: {
                    return ((Number)this.method.invoke(source, EMPTY_ARGS)).longValue();
                }
                case 2: {
                    Number longNumber = (Number)this.method.invoke(source, EMPTY_ARGS);
                    return longNumber == null ? 0L : longNumber.longValue();
                }
                case 6: {
                    Map map = (Map)this.method.invoke(source, EMPTY_ARGS);
                    return map == null ? 0L : (long)map.size();
                }
                case 5: {
                    Collection collection = (Collection)this.method.invoke(source, EMPTY_ARGS);
                    return collection == null ? 0L : (long)collection.size();
                }
                case 7: {
                    Counter counter = (Counter)this.method.invoke(source, EMPTY_ARGS);
                    return counter == null ? 0L : counter.get();
                }
                case 8: {
                    Semaphore semaphore = (Semaphore)this.method.invoke(source, EMPTY_ARGS);
                    return semaphore == null ? 0L : (long)semaphore.availablePermits();
                }
            }
            throw new IllegalStateException("Unrecognized type:" + this.type);
        }
    }
}

