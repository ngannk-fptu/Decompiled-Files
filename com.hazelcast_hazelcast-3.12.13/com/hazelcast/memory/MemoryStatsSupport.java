/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.memory;

import com.hazelcast.util.OperatingSystemMXBeanSupport;

public final class MemoryStatsSupport {
    private static final long TOTAL_PHYSICAL_MEMORY = OperatingSystemMXBeanSupport.readLongAttribute("TotalPhysicalMemorySize", -1L);
    private static final long TOTAL_SWAP_SPACE = OperatingSystemMXBeanSupport.readLongAttribute("TotalSwapSpaceSize", -1L);

    private MemoryStatsSupport() {
    }

    public static long totalPhysicalMemory() {
        return TOTAL_PHYSICAL_MEMORY;
    }

    public static long freePhysicalMemory() {
        return OperatingSystemMXBeanSupport.readLongAttribute("FreePhysicalMemorySize", -1L);
    }

    public static long totalSwapSpace() {
        return TOTAL_SWAP_SPACE;
    }

    public static long freeSwapSpace() {
        return OperatingSystemMXBeanSupport.readLongAttribute("FreeSwapSpaceSize", -1L);
    }
}

