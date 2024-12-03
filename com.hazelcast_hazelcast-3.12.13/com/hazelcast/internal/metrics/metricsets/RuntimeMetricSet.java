/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics.metricsets;

import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.util.Preconditions;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public final class RuntimeMetricSet {
    private RuntimeMetricSet() {
    }

    public static void register(MetricsRegistry metricsRegistry) {
        Preconditions.checkNotNull(metricsRegistry, "metricsRegistry");
        Runtime runtime = Runtime.getRuntime();
        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
        metricsRegistry.register(runtime, "runtime.freeMemory", ProbeLevel.MANDATORY, new LongProbeFunction<Runtime>(){

            @Override
            public long get(Runtime runtime) {
                return runtime.freeMemory();
            }
        });
        metricsRegistry.register(runtime, "runtime.totalMemory", ProbeLevel.MANDATORY, new LongProbeFunction<Runtime>(){

            @Override
            public long get(Runtime runtime) {
                return runtime.totalMemory();
            }
        });
        metricsRegistry.register(runtime, "runtime.maxMemory", ProbeLevel.MANDATORY, new LongProbeFunction<Runtime>(){

            @Override
            public long get(Runtime runtime) {
                return runtime.maxMemory();
            }
        });
        metricsRegistry.register(runtime, "runtime.usedMemory", ProbeLevel.MANDATORY, new LongProbeFunction<Runtime>(){

            @Override
            public long get(Runtime runtime) {
                return runtime.totalMemory() - runtime.freeMemory();
            }
        });
        metricsRegistry.register(runtime, "runtime.availableProcessors", ProbeLevel.MANDATORY, new LongProbeFunction<Runtime>(){

            @Override
            public long get(Runtime runtime) {
                return runtime.availableProcessors();
            }
        });
        metricsRegistry.register(mxBean, "runtime.uptime", ProbeLevel.MANDATORY, new LongProbeFunction<RuntimeMXBean>(){

            @Override
            public long get(RuntimeMXBean runtimeMXBean) {
                return runtimeMXBean.getUptime();
            }
        });
    }
}

