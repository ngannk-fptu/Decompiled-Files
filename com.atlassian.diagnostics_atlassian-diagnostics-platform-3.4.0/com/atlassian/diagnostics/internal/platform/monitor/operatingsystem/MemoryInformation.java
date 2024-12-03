/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.monitor.operatingsystem;

public abstract class MemoryInformation {
    protected long asMegaBytes(long memoryAsBytes) {
        return memoryAsBytes / 0x100000L;
    }
}

