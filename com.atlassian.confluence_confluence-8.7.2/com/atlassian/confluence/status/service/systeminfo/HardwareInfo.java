/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service.systeminfo;

public class HardwareInfo {
    private final int availableProcessors;
    private final long localHomeTotalDiskSize;
    private final long sharedHomeTotalDiskSize;
    private final long totalSystemMemory;

    public HardwareInfo(int availableProcessors, long localHomeTotalDiskSize, long sharedHomeTotalDiskSize, long totalSystemMemory) {
        this.availableProcessors = availableProcessors;
        this.localHomeTotalDiskSize = localHomeTotalDiskSize;
        this.sharedHomeTotalDiskSize = sharedHomeTotalDiskSize;
        this.totalSystemMemory = totalSystemMemory;
    }

    public int getAvailableProcessors() {
        return this.availableProcessors;
    }

    public long getLocalHomeTotalDiskSize() {
        return this.localHomeTotalDiskSize;
    }

    public long getSharedHomeTotalDiskSize() {
        return this.sharedHomeTotalDiskSize;
    }

    public long getTotalSystemMemory() {
        return this.totalSystemMemory;
    }
}

