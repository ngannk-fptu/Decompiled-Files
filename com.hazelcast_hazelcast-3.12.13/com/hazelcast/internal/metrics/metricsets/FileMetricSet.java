/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics.metricsets;

import com.hazelcast.internal.metrics.LongProbeFunction;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.util.Preconditions;
import java.io.File;

public final class FileMetricSet {
    private FileMetricSet() {
    }

    public static void register(MetricsRegistry metricsRegistry) {
        Preconditions.checkNotNull(metricsRegistry, "metricsRegistry");
        File file = new File(System.getProperty("user.home"));
        metricsRegistry.register(file, "file.partition[user.home].freeSpace", ProbeLevel.MANDATORY, new LongProbeFunction<File>(){

            @Override
            public long get(File file) {
                return file.getFreeSpace();
            }
        });
        metricsRegistry.register(file, "file.partition[user.home].totalSpace", ProbeLevel.MANDATORY, new LongProbeFunction<File>(){

            @Override
            public long get(File file) {
                return file.getTotalSpace();
            }
        });
        metricsRegistry.register(file, "file.partition[user.home].usableSpace", ProbeLevel.MANDATORY, new LongProbeFunction<File>(){

            @Override
            public long get(File file) {
                return file.getUsableSpace();
            }
        });
    }
}

