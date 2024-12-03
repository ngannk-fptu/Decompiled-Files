/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics.metricsets;

import com.hazelcast.internal.metrics.DoubleProbeFunction;
import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.OperatingSystemMXBeanSupport;
import com.hazelcast.util.Preconditions;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.util.logging.Level;

public final class OperatingSystemMetricSet {
    private static final ILogger LOGGER = Logger.getLogger(OperatingSystemMetricSet.class);
    private static final long PERCENTAGE_MULTIPLIER = 100L;
    private static final Object[] EMPTY_ARGS = new Object[0];

    private OperatingSystemMetricSet() {
    }

    public static void register(MetricsRegistry metricsRegistry) {
        Preconditions.checkNotNull(metricsRegistry, "metricsRegistry");
        OperatingSystemMXBean mxBean = ManagementFactory.getOperatingSystemMXBean();
        OperatingSystemMetricSet.registerMethod(metricsRegistry, mxBean, "getCommittedVirtualMemorySize", "os.committedVirtualMemorySize");
        OperatingSystemMetricSet.registerMethod(metricsRegistry, mxBean, "getFreePhysicalMemorySize", "os.freePhysicalMemorySize");
        OperatingSystemMetricSet.registerMethod(metricsRegistry, mxBean, "getFreeSwapSpaceSize", "os.freeSwapSpaceSize");
        OperatingSystemMetricSet.registerMethod(metricsRegistry, mxBean, "getProcessCpuTime", "os.processCpuTime");
        OperatingSystemMetricSet.registerMethod(metricsRegistry, mxBean, "getTotalPhysicalMemorySize", "os.totalPhysicalMemorySize");
        OperatingSystemMetricSet.registerMethod(metricsRegistry, mxBean, "getTotalSwapSpaceSize", "os.totalSwapSpaceSize");
        OperatingSystemMetricSet.registerMethod(metricsRegistry, mxBean, "getMaxFileDescriptorCount", "os.maxFileDescriptorCount");
        OperatingSystemMetricSet.registerMethod(metricsRegistry, mxBean, "getOpenFileDescriptorCount", "os.openFileDescriptorCount");
        OperatingSystemMetricSet.registerMethod(metricsRegistry, mxBean, "getProcessCpuLoad", "os.processCpuLoad", 100L);
        OperatingSystemMetricSet.registerMethod(metricsRegistry, mxBean, "getSystemCpuLoad", "os.systemCpuLoad", 100L);
        metricsRegistry.register(mxBean, "os.systemLoadAverage", ProbeLevel.MANDATORY, new DoubleProbeFunction<OperatingSystemMXBean>(){

            @Override
            public double get(OperatingSystemMXBean bean) {
                return bean.getSystemLoadAverage();
            }
        });
    }

    static void registerMethod(MetricsRegistry metricsRegistry, Object osBean, String methodName, String name) {
        if (OperatingSystemMXBeanSupport.GET_FREE_PHYSICAL_MEMORY_SIZE_DISABLED && methodName.equals("getFreePhysicalMemorySize")) {
            metricsRegistry.register(osBean, name, ProbeLevel.MANDATORY, new LongProbeFunction<Object>(){

                @Override
                public long get(Object source) {
                    return -1L;
                }
            });
        } else {
            OperatingSystemMetricSet.registerMethod(metricsRegistry, osBean, methodName, name, 1L);
        }
    }

    private static void registerMethod(MetricsRegistry metricsRegistry, Object osBean, String methodName, String name, final long multiplier) {
        final Method method = OperatingSystemMetricSet.getMethod(osBean, methodName, name);
        if (method == null) {
            return;
        }
        if (Long.TYPE.equals(method.getReturnType())) {
            metricsRegistry.register(osBean, name, ProbeLevel.MANDATORY, new LongProbeFunction(){

                public long get(Object bean) throws Exception {
                    return (Long)method.invoke(bean, EMPTY_ARGS) * multiplier;
                }
            });
        } else {
            metricsRegistry.register(osBean, name, ProbeLevel.MANDATORY, new DoubleProbeFunction(){

                public double get(Object bean) throws Exception {
                    return (Double)method.invoke(bean, EMPTY_ARGS) * (double)multiplier;
                }
            });
        }
    }

    private static Method getMethod(Object source, String methodName, String name) {
        try {
            Method method = source.getClass().getMethod(methodName, new Class[0]);
            method.setAccessible(true);
            return method;
        }
        catch (Exception e) {
            if (LOGGER.isFinestEnabled()) {
                LOGGER.log(Level.FINEST, "Unable to register OperatingSystemMXBean method " + methodName + " used for probe " + name, e);
            }
            return null;
        }
    }
}

