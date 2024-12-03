/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.packed.PackedInts;

public class GrowableWriter
implements PackedInts.Mutable {
    private long currentMask;
    private PackedInts.Mutable current;
    private final float acceptableOverheadRatio;

    public GrowableWriter(int startBitsPerValue, int valueCount, float acceptableOverheadRatio) {
        this.acceptableOverheadRatio = acceptableOverheadRatio;
        this.current = PackedInts.getMutable(valueCount, startBitsPerValue, this.acceptableOverheadRatio);
        this.currentMask = GrowableWriter.mask(this.current.getBitsPerValue());
    }

    private static long mask(int bitsPerValue) {
        return bitsPerValue == 64 ? -1L : PackedInts.maxValue(bitsPerValue);
    }

    @Override
    public long get(int index) {
        return this.current.get(index);
    }

    @Override
    public int size() {
        return this.current.size();
    }

    @Override
    public int getBitsPerValue() {
        return this.current.getBitsPerValue();
    }

    public PackedInts.Mutable getMutable() {
        return this.current;
    }

    @Override
    public Object getArray() {
        return this.current.getArray();
    }

    @Override
    public boolean hasArray() {
        return this.current.hasArray();
    }

    private void ensureCapacity(long value) {
        int bitsRequired;
        if ((value & this.currentMask) == value) {
            return;
        }
        int n = bitsRequired = value < 0L ? 64 : PackedInts.bitsRequired(value);
        assert (bitsRequired > this.current.getBitsPerValue());
        int valueCount = this.size();
        PackedInts.Mutable next = PackedInts.getMutable(valueCount, bitsRequired, this.acceptableOverheadRatio);
        PackedInts.copy((PackedInts.Reader)this.current, 0, next, 0, valueCount, 1024);
        this.current = next;
        this.currentMask = GrowableWriter.mask(this.current.getBitsPerValue());
    }

    @Override
    public void set(int index, long value) {
        this.ensureCapacity(value);
        this.current.set(index, value);
    }

    @Override
    public void clear() {
        this.current.clear();
    }

    public GrowableWriter resize(int newSize) {
        GrowableWriter next = new GrowableWriter(this.getBitsPerValue(), newSize, this.acceptableOverheadRatio);
        int limit = Math.min(this.size(), newSize);
        PackedInts.copy((PackedInts.Reader)this.current, 0, (PackedInts.Mutable)next, 0, limit, 1024);
        return next;
    }

    @Override
    public int get(int index, long[] arr, int off, int len) {
        return this.current.get(index, arr, off, len);
    }

    @Override
    public int set(int index, long[] arr, int off, int len) {
        long max = 0L;
        int end = off + len;
        for (int i = off; i < end; ++i) {
            max |= arr[i];
        }
        this.ensureCapacity(max);
        return this.current.set(index, arr, off, len);
    }

    @Override
    public void fill(int fromIndex, int toIndex, long val) {
        this.ensureCapacity(val);
        this.current.fill(fromIndex, toIndex, val);
    }

    @Override
    public long ramBytesUsed() {
        return RamUsageEstimator.alignObjectSize(RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + RamUsageEstimator.NUM_BYTES_OBJECT_REF + 8 + 4) + this.current.ramBytesUsed();
    }

    @Override
    public void save(DataOutput out) throws IOException {
        this.current.save(out);
    }
}

