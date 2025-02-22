/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.memory;

import com.hazelcast.memory.DefaultGarbageCollectorStats;
import com.hazelcast.memory.GarbageCollectorStats;
import com.hazelcast.util.SetUtil;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.Set;

public final class GCStatsSupport {
    private static final Set<String> YOUNG_GC;
    private static final Set<String> OLD_GC;

    private GCStatsSupport() {
    }

    static void fill(DefaultGarbageCollectorStats stats) {
        long minorCount = 0L;
        long minorTime = 0L;
        long majorCount = 0L;
        long majorTime = 0L;
        long unknownCount = 0L;
        long unknownTime = 0L;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            long count = gc.getCollectionCount();
            if (count < 0L) continue;
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
        stats.setMajorCount(majorCount);
        stats.setMajorTime(majorTime);
        stats.setMinorCount(minorCount);
        stats.setMinorTime(minorTime);
        stats.setUnknownCount(unknownCount);
        stats.setUnknownTime(unknownTime);
    }

    public static GarbageCollectorStats getGCStats() {
        DefaultGarbageCollectorStats stats = new DefaultGarbageCollectorStats();
        GCStatsSupport.fill(stats);
        return stats;
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
}

