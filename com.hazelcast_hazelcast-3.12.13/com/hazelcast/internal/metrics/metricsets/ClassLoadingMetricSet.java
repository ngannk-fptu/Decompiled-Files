/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics.metricsets;

import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.util.Preconditions;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

public final class ClassLoadingMetricSet {
    private ClassLoadingMetricSet() {
    }

    public static void register(MetricsRegistry metricsRegistry) {
        Preconditions.checkNotNull(metricsRegistry, "metricsRegistry");
        ClassLoadingMXBean mxBean = ManagementFactory.getClassLoadingMXBean();
        metricsRegistry.register(mxBean, "classloading.loadedClassesCount", ProbeLevel.MANDATORY, new LongProbeFunction<ClassLoadingMXBean>(){

            @Override
            public long get(ClassLoadingMXBean classLoadingMXBean) {
                return classLoadingMXBean.getLoadedClassCount();
            }
        });
        metricsRegistry.register(mxBean, "classloading.totalLoadedClassesCount", ProbeLevel.MANDATORY, new LongProbeFunction<ClassLoadingMXBean>(){

            @Override
            public long get(ClassLoadingMXBean classLoadingMXBean) {
                return classLoadingMXBean.getTotalLoadedClassCount();
            }
        });
        metricsRegistry.register(mxBean, "classloading.unloadedClassCount", ProbeLevel.MANDATORY, new LongProbeFunction<ClassLoadingMXBean>(){

            @Override
            public long get(ClassLoadingMXBean classLoadingMXBean) {
                return classLoadingMXBean.getUnloadedClassCount();
            }
        });
    }
}

