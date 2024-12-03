/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.packed.PackedInts;

final class Direct8
extends PackedInts.MutableImpl {
    final byte[] values;

    Direct8(int valueCount) {
        super(valueCount, 8);
        this.values = new byte[valueCount];
    }

    Direct8(int packedIntsVersion, DataInput in, int valueCount) throws IOException {
        this(valueCount);
        in.readBytes(this.values, 0, valueCount);
        int remaining = (int)(PackedInts.Format.PACKED.byteCount(packedIntsVersion, valueCount, 8) - 1L * (long)valueCount);
        for (int i = 0; i < remaining; ++i) {
            in.readByte();
        }
    }

    @Override
    public long get(int index) {
        return (long)this.values[index] & 0xFFL;
    }

    @Override
    public void set(int index, long value) {
        this.values[index] = (byte)value;
    }

    @Override
    public long ramBytesUsed() {
        return RamUsageEstimator.alignObjectSize(RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 8 + RamUsageEstimator.NUM_BYTES_OBJECT_REF) + RamUsageEstimator.sizeOf(this.values);
    }

    @Override
    public void clear() {
        Arrays.fill(this.values, (byte)0);
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
        int i = index;
        int o = off;
        int end = index + gets;
        while (i < end) {
            arr[o] = (long)this.values[i] & 0xFFL;
            ++i;
            ++o;
        }
        return gets;
    }

    @Override
    public int set(int index, long[] arr, int off, int len) {
        assert (len > 0) : "len must be > 0 (got " + len + ")";
        assert (index >= 0 && index < this.valueCount);
        assert (off + len <= arr.length);
        int sets = Math.min(this.valueCount - index, len);
        int i = index;
        int o = off;
        int end = index + sets;
        while (i < end) {
            this.values[i] = (byte)arr[o];
            ++i;
            ++o;
        }
        return sets;
    }

    @Override
    public void fill(int fromIndex, int toIndex, long val) {
        assert (val == (val & 0xFFL));
        Arrays.fill(this.values, fromIndex, toIndex, (byte)val);
    }
}

