/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util.packed;

import com.atlassian.lucene36.store.DataInput;
import com.atlassian.lucene36.util.RamUsageEstimator;
import com.atlassian.lucene36.util.packed.PackedInts;
import java.io.IOException;
import java.util.Arrays;

class Direct32
extends PackedInts.ReaderImpl
implements PackedInts.Mutable {
    private int[] values;
    private static final int BITS_PER_VALUE = 32;

    public Direct32(int valueCount) {
        super(valueCount, 32);
        this.values = new int[valueCount];
    }

    public Direct32(DataInput in, int valueCount) throws IOException {
        super(valueCount, 32);
        int[] values = new int[valueCount];
        for (int i = 0; i < valueCount; ++i) {
            values[i] = in.readInt();
        }
        int mod = valueCount % 2;
        if (mod != 0) {
            in.readInt();
        }
        this.values = values;
    }

    public Direct32(int[] values) {
        super(values.length, 32);
        this.values = values;
    }

    public long get(int index) {
        assert (index >= 0 && index < this.size());
        return 0xFFFFFFFFL & (long)this.values[index];
    }

    public void set(int index, long value) {
        this.values[index] = (int)(value & 0xFFFFFFFFFFFFFFFFL);
    }

    public long ramBytesUsed() {
        return RamUsageEstimator.sizeOf(this.values);
    }

    public void clear() {
        Arrays.fill(this.values, 0);
    }

    public int[] getArray() {
        return this.values;
    }

    public boolean hasArray() {
        return true;
    }
}

