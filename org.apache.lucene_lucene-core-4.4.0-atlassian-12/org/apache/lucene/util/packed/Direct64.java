/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.packed.PackedInts;

final class Direct64
extends PackedInts.MutableImpl {
    final long[] values;

    Direct64(int valueCount) {
        super(valueCount, 64);
        this.values = new long[valueCount];
    }

    Direct64(int packedIntsVersion, DataInput in, int valueCount) throws IOException {
        this(valueCount);
        for (int i = 0; i < valueCount; ++i) {
            this.values[i] = in.readLong();
        }
    }

    @Override
    public long get(int index) {
        return this.values[index];
    }

    @Override
    public void set(int index, long value) {
        this.values[index] = value;
    }

    @Override
    public long ramBytesUsed() {
        return RamUsageEstimator.alignObjectSize(RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 8 + RamUsageEstimator.NUM_BYTES_OBJECT_REF) + RamUsageEstimator.sizeOf(this.values);
    }

    @Override
    public void clear() {
        Arrays.fill(this.values, 0L);
    }

    @Override
    public Object getArray() {
        return this.values;
    }

    @Override
    public boolean hasArray() {
        return true;
    }

    @Override
    public int get(int index, long[] arr, int off, int len) {
        assert (len > 0) : "len must be > 0 (got " + len + ")";
        assert (index >= 0 && index < this.valueCount);
        assert (off + len <= arr.length);
        int gets = Math.min(this.valueCount - index, len);
        System.arraycopy(this.values, index, arr, off, gets);
        return gets;
    }

    @Override
    public int set(int index, long[] arr, int off, int len) {
        assert (len > 0) : "len must be > 0 (got " + len + ")";
        assert (index >= 0 && index < this.valueCount);
        assert (off + len <= arr.length);
        int sets = Math.min(this.valueCount - index, len);
        System.arraycopy(arr, off, this.values, index, sets);
        return sets;
    }

    @Override
    public void fill(int fromIndex, int toIndex, long val) {
        Arrays.fill(this.values, fromIndex, toIndex, val);
    }
}

