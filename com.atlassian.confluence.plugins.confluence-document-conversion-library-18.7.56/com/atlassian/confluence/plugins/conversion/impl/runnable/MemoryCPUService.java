/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.conversion.impl.runnable;

import com.atlassian.confluence.plugins.conversion.impl.runnable.MemoryCPUInfo;

public interface MemoryCPUService {
    public MemoryCPUInfo getMemoryCPUInfo();

    public long getTotalFreeMemory();
}

