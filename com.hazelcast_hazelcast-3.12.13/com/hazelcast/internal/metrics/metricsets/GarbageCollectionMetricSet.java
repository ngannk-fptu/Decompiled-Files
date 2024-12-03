/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.metrics.metricsets;

import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.SetUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class GarbageCollectionMetricSet {
    private static final Set<String> YOUNG_GC;
    private static final Set<String> OLD_GC;
    private static final int PUBLISH_FREQUENCY_SECONDS = 1;

    private GarbageCollectionMetricSet() {
    }

    public static void register(MetricsRegistry metricsRegistry) {
        Preconditions.checkNotNull(metricsRegistry, "metricsRegistry");
        GcStats stats = new GcStats();
        metricsRegistry.scheduleAtFixedRate(stats, 1L, TimeUnit.SECONDS, ProbeLevel.MANDATORY);
        metricsRegistry.scanAndRegister(stats, "gc");
    }

    static {
        Set<String> youngGC = SetUtil.createHashSet(4);
        youngGC.add("PS Scavenge");
        youngGC.add("ParNew");
        youngGC.add("G1 Young Generation");
        youngGC.add("Copy");
        YOUNG_GC = Collections.unmodifiableSet(youngGC);
        Set<String> oldGC = SetUtil.createHashSet(5);
        oldGC.add("PS MarkSweep");
        oldGC.add("ConcurrentMarkSweep");
        oldGC.add("G1 Old Generation");
        oldGC.add("G1 Mixed Generation");
        oldGC.add("MarkSweepCompact");
        OLD_GC = Collections.unmodifiableSet(oldGC);
    }

    @SuppressFBWarnings(value={"URF_UNREAD_FIELD"}, justification="used by instrumentation tools")
    static class GcStats
    implements Runnable {
        @Probe(level=ProbeLevel.MANDATORY)
        volatile long minorCount;
        @Probe(level=ProbeLevel.MANDATORY)
        volatile long minorTime;
        @Probe(level=ProbeLevel.MANDATORY)
        volatile long majorCount;
        @Probe(level=ProbeLevel.MANDATORY)
        volatile long majorTime;
        @Probe(level=ProbeLevel.MANDATORY)
        volatile long unknownCount;
        @Probe(level=ProbeLevel.MANDATORY)
        volatile long unknownTime;

        GcStats() {
        }

        @Override
        public void run() {
            long minorCount = 0L;
            long minorTime = 0L;
            long majorCount = 0L;
            long majorTime = 0L;
            long unknownCount = 0L;
            long unknownTime = 0L;
            for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
                long count = gc.getCollectionCount();
                if (count == -1L) continue;
                if (YOUNG_GC.contains(gc.getName())) {
                    minorCount += count;
                    minorTime += gc.getCollectionTime();
                    continue;
                }
                if (OLD_GC.contains(gc.getName())) {
                    majorCount += count;
                    majorTime += gc.getCollectionTime();
                    continue;
                }
                unknownCount += count;
                unknownTime += gc.getCollectionTime();
            }
            this.minorCount = minorCount;
            this.minorTime = minorTime;
            this.majorCount = majorCount;
            this.majorTime = majorTime;
            this.unknownCount = unknownCount;
            this.unknownTime = unknownTime;
        }
    }
}

