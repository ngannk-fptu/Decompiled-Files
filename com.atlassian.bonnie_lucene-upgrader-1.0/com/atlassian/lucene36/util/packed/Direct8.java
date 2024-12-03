/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util.packed;

import com.atlassian.lucene36.store.DataInput;
import com.atlassian.lucene36.util.RamUsageEstimator;
import com.atlassian.lucene36.util.packed.PackedInts;
import java.io.IOException;
import java.util.Arrays;

class Direct8
extends PackedInts.ReaderImpl
implements PackedInts.Mutable {
    private byte[] values;
    private static final int BITS_PER_VALUE = 8;

    public Direct8(int valueCount) {
        super(valueCount, 8);
        this.values = new byte[valueCount];
    }

    public Direct8(DataInput in, int valueCount) throws IOException {
        super(valueCount, 8);
        byte[] values = new byte[valueCount];
        for (int i = 0; i < valueCount; ++i) {
            values[i] = in.readByte();
        }
        int mod = valueCount % 8;
        if (mod != 0) {
            int pad = 8 - mod;
            for (int i = 0; i < pad; ++i) {
                in.readByte();
            }
        }
        this.values = values;
    }

    public Direct8(byte[] values) {
        super(values.length, 8);
        this.values = values;
    }

    public long get(int index) {
        assert (index >= 0 && index < this.size());
        return 0xFFL & (long)this.values[index];
    }

    public void set(int index, long value) {
        this.values[index] = (byte)(value & 0xFFL);
    }

    public long ramBytesUsed() {
        return RamUsageEstimator.sizeOf(this.values);
    }

    public void clear() {
        Arrays.fill(this.values, (byte)0);
    }

    public Object getArray() {
        return this.values;
    }

    public boolean hasArray() {
        return true;
    }
}

