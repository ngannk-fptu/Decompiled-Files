/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.binder.jvm;

import io.micrometer.common.lang.Nullable;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

class JvmMemory {
    private JvmMemory() {
    }

    static Stream<MemoryPoolMXBean> getLongLivedHeapPools() {
        return ManagementFactory.getMemoryPoolMXBeans().stream().filter(JvmMemory::isHeap).filter(mem -> JvmMemory.isLongLivedPool(mem.getName()));
    }

    static boolean isConcurrentPhase(String cause, String name) {
        return "No GC".equals(cause) || "Shenandoah Cycles".equals(name) || "ZGC Cycles".equals(name) || name.startsWith("GPGC") && !name.endsWith("Pauses");
    }

    static boolean isAllocationPool(String name) {
        return name != null && (name.endsWith("Eden Space") || "Shenandoah".equals(name) || "ZHeap".equals(name) || name.endsWith("New Gen") || name.endsWith("nursery-allocate") || name.endsWith("-eden") || "JavaHeap".equals(name));
    }

    static boolean isLongLivedPool(String name) {
        return name != null && (name.endsWith("Old Gen") || name.endsWith("Tenured Gen") || "Shenandoah".equals(name) || "ZHeap".equals(name) || name.endsWith("balanced-old") || name.contains("tenured") || "JavaHeap".equals(name));
    }

    static boolean isHeap(MemoryPoolMXBean memoryPoolBean) {
        return MemoryType.HEAP.equals((Object)memoryPoolBean.getType());
    }

    static double getUsageValue(MemoryPoolMXBean memoryPoolMXBean, ToLongFunction<MemoryUsage> getter) {
        MemoryUsage usage = JvmMemory.getUsage(memoryPoolMXBean);
        if (usage == null) {
            return Double.NaN;
        }
        return getter.applyAsLong(usage);
    }

    @Nullable
    private static MemoryUsage getUsage(MemoryPoolMXBean memoryPoolMXBean) {
        try {
            return memoryPoolMXBean.getUsage();
        }
        catch (InternalError e) {
            return null;
        }
    }
}

