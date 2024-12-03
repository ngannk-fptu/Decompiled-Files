/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics.metricsets;

import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.util.Preconditions;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public final class ThreadMetricSet {
    private ThreadMetricSet() {
    }

    public static void register(MetricsRegistry metricsRegistry) {
        Preconditions.checkNotNull(metricsRegistry, "metricsRegistry");
        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        metricsRegistry.register(mxBean, "thread.threadCount", ProbeLevel.MANDATORY, new LongProbeFunction<ThreadMXBean>(){

            @Override
            public long get(ThreadMXBean threadMXBean) {
                return threadMXBean.getThreadCount();
            }
        });
        metricsRegistry.register(mxBean, "thread.peakThreadCount", ProbeLevel.MANDATORY, new LongProbeFunction<ThreadMXBean>(){

            @Override
            public long get(ThreadMXBean threadMXBean) {
                return threadMXBean.getPeakThreadCount();
            }
        });
        metricsRegistry.register(mxBean, "thread.daemonThreadCount", ProbeLevel.MANDATORY, new LongProbeFunction<ThreadMXBean>(){

            @Override
            public long get(ThreadMXBean threadMXBean) {
                return threadMXBean.getDaemonThreadCount();
            }
        });
        metricsRegistry.register(mxBean, "thread.totalStartedThreadCount", ProbeLevel.MANDATORY, new LongProbeFunction<ThreadMXBean>(){

            @Override
            public long get(ThreadMXBean threadMXBean) {
                return threadMXBean.getTotalStartedThreadCount();
            }
        });
    }
}

