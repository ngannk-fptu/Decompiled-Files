/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util.packed;

import com.atlassian.lucene36.util.packed.PackedInts;

public class GrowableWriter
implements PackedInts.Mutable {
    private long currentMaxValue;
    private PackedInts.Mutable current;
    private final boolean roundFixedSize;

    public GrowableWriter(int startBitsPerValue, int valueCount, boolean roundFixedSize) {
        this.roundFixedSize = roundFixedSize;
        this.current = PackedInts.getMutable(valueCount, this.getSize(startBitsPerValue));
        this.currentMaxValue = PackedInts.maxValue(this.current.getBitsPerValue());
    }

    private final int getSize(int bpv) {
        if (this.roundFixedSize) {
            return PackedInts.getNextFixedSize(bpv);
        }
        return bpv;
    }

    public long get(int index) {
        return this.current.get(index);
    }

    public int size() {
        return this.current.size();
    }

    public int getBitsPerValue() {
        return this.current.getBitsPerValue();
    }

    public PackedInts.Mutable getMutable() {
        return this.current;
    }

    public Object getArray() {
        return this.current.getArray();
    }

    public boolean hasArray() {
        return this.current.hasArray();
    }

    public void set(int index, long value) {
        if (value >= this.currentMaxValue) {
            int bpv = this.getBitsPerValue();
            while (this.currentMaxValue <= value && this.currentMaxValue != Long.MAX_VALUE) {
                ++bpv;
                this.currentMaxValue *= 2L;
            }
            int valueCount = this.size();
            PackedInts.Mutable next = PackedInts.getMutable(valueCount, this.getSize(bpv));
            for (int i = 0; i < valueCount; ++i) {
                next.set(i, this.current.get(i));
            }
            this.current = next;
            this.currentMaxValue = PackedInts.maxValue(this.current.getBitsPerValue());
        }
        this.current.set(index, value);
    }

    public void clear() {
        this.current.clear();
    }

    public GrowableWriter resize(int newSize) {
        GrowableWriter next = new GrowableWriter(this.getBitsPerValue(), newSize, this.roundFixedSize);
        int limit = Math.min(this.size(), newSize);
        for (int i = 0; i < limit; ++i) {
            next.set(i, this.get(i));
        }
        return next;
    }
}

