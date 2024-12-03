/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util.packed;

import com.atlassian.lucene36.store.DataInput;
import com.atlassian.lucene36.util.RamUsageEstimator;
import com.atlassian.lucene36.util.packed.PackedInts;
import java.io.IOException;
import java.util.Arrays;

class Direct64
extends PackedInts.ReaderImpl
implements PackedInts.Mutable {
    private long[] values;
    private static final int BITS_PER_VALUE = 64;

    public Direct64(int valueCount) {
        super(valueCount, 64);
        this.values = new long[valueCount];
    }

    public Direct64(DataInput in, int valueCount) throws IOException {
        super(valueCount, 64);
        long[] values = new long[valueCount];
        for (int i = 0; i < valueCount; ++i) {
            values[i] = in.readLong();
        }
        this.values = values;
    }

    public Direct64(long[] values) {
        super(values.length, 64);
        this.values = values;
    }

    public long get(int index) {
        assert (index >= 0 && index < this.size());
        return this.values[index];
    }

    public void set(int index, long value) {
        this.values[index] = value;
    }

    public long ramBytesUsed() {
        return RamUsageEstimator.sizeOf(this.values);
    }

    public void clear() {
        Arrays.fill(this.values, 0L);
    }

    public long[] getArray() {
        return this.values;
    }

    public boolean hasArray() {
        return true;
    }
}

