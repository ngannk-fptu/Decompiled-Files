/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.conversion.impl.runnable;

public interface MemoryReserveService {
    public boolean reserveMemory(long var1);

    public void releaseMemory(long var1);
}

