/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.sort;

import com.hazelcast.internal.memory.MemoryAccessor;
import com.hazelcast.internal.util.sort.MemArrayQuickSorter;

public class IntMemArrayQuickSorter
extends MemArrayQuickSorter {
    private int pivot;

    public IntMemArrayQuickSorter(MemoryAccessor mem, long baseAddress) {
        super(mem, baseAddress);
    }

    @Override
    protected void loadPivot(long index) {
        this.pivot = this.intAtIndex(index);
    }

    @Override
    protected boolean isLessThanPivot(long index) {
        return this.intAtIndex(index) < this.pivot;
    }

    @Override
    protected boolean isGreaterThanPivot(long index) {
        return this.intAtIndex(index) > this.pivot;
    }

    @Override
    protected void swap(long index1, long index2) {
        long addrOfIndex1 = this.addrOfIndex(index1);
        long addrOfIndex2 = this.addrOfIndex(index2);
        int tmp = this.intAtAddress(addrOfIndex1);
        this.mem.putInt(addrOfIndex1, this.intAtAddress(addrOfIndex2));
        this.mem.putInt(addrOfIndex2, tmp);
    }

    private long addrOfIndex(long index) {
        return this.baseAddress + 4L * index;
    }

    private int intAtIndex(long index) {
        return this.intAtAddress(this.addrOfIndex(index));
    }

    private int intAtAddress(long address) {
        return this.mem.getInt(address);
    }
}

