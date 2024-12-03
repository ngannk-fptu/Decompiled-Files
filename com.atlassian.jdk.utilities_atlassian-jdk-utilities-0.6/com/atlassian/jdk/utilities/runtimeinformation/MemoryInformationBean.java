/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jdk.utilities.runtimeinformation;

import com.atlassian.jdk.utilities.runtimeinformation.MemoryInformation;
import java.lang.management.MemoryPoolMXBean;

public class MemoryInformationBean
implements MemoryInformation {
    private final MemoryPoolMXBean memoryPool;

    MemoryInformationBean(MemoryPoolMXBean memoryPool) {
        this.memoryPool = memoryPool;
    }

    @Override
    public String getName() {
        return this.memoryPool.getName();
    }

    @Override
    public long getTotal() {
        return this.memoryPool.getUsage().getMax();
    }

    @Override
    public long getUsed() {
        return this.memoryPool.getUsage().getUsed();
    }

    @Override
    public long getFree() {
        return this.getTotal() - this.getUsed();
    }

    public String toString() {
        return this.memoryPool.getName() + ": " + this.memoryPool.getUsage();
    }
}

