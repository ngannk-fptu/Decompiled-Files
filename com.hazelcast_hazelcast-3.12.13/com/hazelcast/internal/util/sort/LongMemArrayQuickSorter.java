/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.sort;

import com.hazelcast.internal.memory.MemoryAccessor;
import com.hazelcast.internal.util.sort.MemArrayQuickSorter;

public class LongMemArrayQuickSorter
extends MemArrayQuickSorter {
    private long pivot;

    public LongMemArrayQuickSorter(MemoryAccessor mem, long baseAddress) {
        super(mem, baseAddress);
    }

    @Override
    protected void loadPivot(long index) {
        this.pivot = this.longAtIndex(index);
    }

    @Override
    protected boolean isLessThanPivot(long index) {
        return this.longAtIndex(index) < this.pivot;
    }

    @Override
    protected boolean isGreaterThanPivot(long index) {
        return this.longAtIndex(index) > this.pivot;
    }

    @Override
    protected void swap(long index1, long index2) {
        long addrOfIndex1 = this.addrOfIndex(index1);
        long addrOfIndex2 = this.addrOfIndex(index2);
        long tmp = this.longAtAddress(addrOfIndex1);
        this.mem.putLong(addrOfIndex1, this.longAtAddress(addrOfIndex2));
        this.mem.putLong(addrOfIndex2, tmp);
    }

    private long addrOfIndex(long index) {
        return this.baseAddress + 8L * index;
    }

    private long longAtIndex(long index) {
        return this.longAtAddress(this.addrOfIndex(index));
    }

    private long longAtAddress(long address) {
        return this.mem.getLong(address);
    }
}

