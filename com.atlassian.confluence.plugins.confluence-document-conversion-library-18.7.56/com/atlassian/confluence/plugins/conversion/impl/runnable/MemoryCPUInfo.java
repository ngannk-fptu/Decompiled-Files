/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.conversion.impl.runnable;

import com.atlassian.confluence.plugins.conversion.impl.ConfigurationProperties;

public class MemoryCPUInfo {
    private static final double SYSTEM_LOAD_RATIO = ConfigurationProperties.getDouble(ConfigurationProperties.PROP_SYSTEM_LOAD_RATIO);
    private static final double USED_MEMORY_RATIO = ConfigurationProperties.getDouble(ConfigurationProperties.PROP_USED_MEMORY_RATIO);
    private long maxHeap;
    private long freeMemory;
    private int availableProcessors;
    private double systemLoadAverage;

    public long getMaxHeap() {
        return this.maxHeap;
    }

    public void setMaxHeap(long maxHeap) {
        this.maxHeap = maxHeap;
    }

    public long getFreeMemory() {
        return this.freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public long getUsedMemory() {
        return this.maxHeap - this.freeMemory;
    }

    public int getAvailableProcessors() {
        return this.availableProcessors;
    }

    public void setAvailableProcessors(int availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public double getSystemLoadAverage() {
        return this.systemLoadAverage;
    }

    public void setSystemLoadAverage(double systemLoadAverage) {
        this.systemLoadAverage = systemLoadAverage;
    }

    public static Double getUsedMemoryRatio() {
        return USED_MEMORY_RATIO;
    }

    public static Double getSystemLoadRatio() {
        return SYSTEM_LOAD_RATIO;
    }

    public boolean isLowMemory() {
        return (double)this.getUsedMemory() > (double)this.maxHeap * USED_MEMORY_RATIO;
    }

    public boolean isCPUBusy() {
        return this.systemLoadAverage > (double)this.availableProcessors * SYSTEM_LOAD_RATIO;
    }
}

