/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.sort;

import com.hazelcast.internal.memory.MemoryAccessor;
import com.hazelcast.internal.util.sort.QuickSorter;

public abstract class MemArrayQuickSorter
extends QuickSorter {
    protected final MemoryAccessor mem;
    protected long baseAddress;

    protected MemArrayQuickSorter(MemoryAccessor mem, long baseAddress) {
        this.mem = mem;
        this.baseAddress = baseAddress;
    }

    public MemArrayQuickSorter gotoAddress(long baseAddress) {
        this.baseAddress = baseAddress;
        return this;
    }
}

