/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.util.MemoryInfoAccessor;

public class RuntimeMemoryInfoAccessor
implements MemoryInfoAccessor {
    @Override
    public long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    @Override
    public long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    @Override
    public long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }
}

