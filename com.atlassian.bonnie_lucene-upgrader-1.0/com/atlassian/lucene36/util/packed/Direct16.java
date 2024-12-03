/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util.packed;

import com.atlassian.lucene36.store.DataInput;
import com.atlassian.lucene36.util.RamUsageEstimator;
import com.atlassian.lucene36.util.packed.PackedInts;
import java.io.IOException;
import java.util.Arrays;

class Direct16
extends PackedInts.ReaderImpl
implements PackedInts.Mutable {
    private short[] values;
    private static final int BITS_PER_VALUE = 16;

    public Direct16(int valueCount) {
        super(valueCount, 16);
        this.values = new short[valueCount];
    }

    public Direct16(DataInput in, int valueCount) throws IOException {
        super(valueCount, 16);
        short[] values = new short[valueCount];
        for (int i = 0; i < valueCount; ++i) {
            values[i] = in.readShort();
        }
        int mod = valueCount % 4;
        if (mod != 0) {
            int pad = 4 - mod;
            for (int i = 0; i < pad; ++i) {
                in.readShort();
            }
        }
        this.values = values;
    }

    public Direct16(short[] values) {
        super(values.length, 16);
        this.values = values;
    }

    public long get(int index) {
        assert (index >= 0 && index < this.size());
        return 0xFFFFL & (long)this.values[index];
    }

    public void set(int index, long value) {
        this.values[index] = (short)(value & 0xFFFFL);
    }

    public long ramBytesUsed() {
        return RamUsageEstimator.sizeOf(this.values);
    }

    public void clear() {
        Arrays.fill(this.values, (short)0);
    }

    public Object getArray() {
        return this.values;
    }

    public boolean hasArray() {
        return true;
    }
}

