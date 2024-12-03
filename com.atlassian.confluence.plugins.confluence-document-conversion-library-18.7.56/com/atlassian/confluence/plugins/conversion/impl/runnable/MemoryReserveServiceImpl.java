/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.conversion.impl.runnable;

import com.atlassian.confluence.plugins.conversion.impl.runnable.MemoryCPUService;
import com.atlassian.confluence.plugins.conversion.impl.runnable.MemoryReserveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="memoryReserveServiceImpl")
public class MemoryReserveServiceImpl
implements MemoryReserveService {
    private static final long INTERCEPT = 20L;
    private static final double SLOPE = 2.5;
    private static final long MINIMUM_MB = 100L;
    private static final long ONE_MB = 0x100000L;
    private long reservedMemory = 0L;
    private final MemoryCPUService memoryCPUService;

    @Autowired
    public MemoryReserveServiceImpl(MemoryCPUService memoryCPUService) {
        this.memoryCPUService = memoryCPUService;
    }

    @Override
    public boolean reserveMemory(long fileSize) {
        long fileSizeInMB = fileSize / 0x100000L;
        long memoryRequired = this.getMemoryRequired(fileSizeInMB);
        long freeMemory = this.memoryCPUService.getTotalFreeMemory();
        return this.reserveMemory(freeMemory / 0x100000L, memoryRequired);
    }

    private synchronized boolean reserveMemory(long freeMemory, long memoryRequired) {
        if (freeMemory > this.reservedMemory + memoryRequired + 100L) {
            this.reservedMemory += memoryRequired;
            return true;
        }
        return false;
    }

    private long getMemoryRequired(long fileSizeInMB) {
        return new Double(20.0 + 2.5 * (double)fileSizeInMB).longValue();
    }

    @Override
    public synchronized void releaseMemory(long fileSize) {
        this.reservedMemory -= this.getMemoryRequired(fileSize / 0x100000L);
    }

    public long getReservedMemory() {
        return this.reservedMemory;
    }
}

