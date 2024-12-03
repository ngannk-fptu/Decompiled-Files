/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.conversion.impl.runnable;

import com.atlassian.confluence.plugins.conversion.impl.runnable.MemoryCPUInfo;
import com.atlassian.confluence.plugins.conversion.impl.runnable.MemoryCPUService;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import org.springframework.stereotype.Component;

@Component(value="memoryCPUService")
public class MemoryCPUServiceImpl
implements MemoryCPUService {
    @Override
    public MemoryCPUInfo getMemoryCPUInfo() {
        MemoryCPUInfo info = new MemoryCPUInfo();
        info.setMaxHeap(Runtime.getRuntime().maxMemory());
        info.setFreeMemory(Runtime.getRuntime().freeMemory());
        OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
        info.setAvailableProcessors(bean.getAvailableProcessors());
        info.setSystemLoadAverage(bean.getSystemLoadAverage());
        return info;
    }

    @Override
    public long getTotalFreeMemory() {
        long freeHeapMb = Runtime.getRuntime().freeMemory();
        long maxHeapMb = Runtime.getRuntime().maxMemory();
        long totalHeapMb = Runtime.getRuntime().totalMemory();
        long usedHeapMb = totalHeapMb - freeHeapMb;
        return maxHeapMb - usedHeapMb;
    }
}

