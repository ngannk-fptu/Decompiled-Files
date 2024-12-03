/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.util.Arrays;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.packed.PackedInts;

abstract class AbstractAppendingLongBuffer {
    static final int MIN_PAGE_SIZE = 64;
    static final int MAX_PAGE_SIZE = 0x100000;
    final int pageShift;
    final int pageMask;
    long[] minValues;
    PackedInts.Reader[] deltas;
    private long deltasBytes;
    int valuesOff;
    final long[] pending;
    int pendingOff;

    AbstractAppendingLongBuffer(int initialBlockCount, int pageSize) {
        this.minValues = new long[initialBlockCount];
        this.deltas = new PackedInts.Reader[initialBlockCount];
        this.pending = new long[pageSize];
        this.pageShift = PackedInts.checkBlockSize(pageSize, 64, 0x100000);
        this.pageMask = pageSize - 1;
        this.valuesOff = 0;
        this.pendingOff = 0;
    }

    public final long size() {
        return (long)this.valuesOff * (long)this.pending.length + (long)this.pendingOff;
    }

    public final void add(long l) {
        if (this.pendingOff == this.pending.length) {
            if (this.deltas.length == this.valuesOff) {
                int newLength = ArrayUtil.oversize(this.valuesOff + 1, 8);
                this.grow(newLength);
            }
            this.packPendingValues();
            if (this.deltas[this.valuesOff] != null) {
                this.deltasBytes += this.deltas[this.valuesOff].ramBytesUsed();
            }
            ++this.valuesOff;
            this.pendingOff = 0;
        }
        this.pending[this.pendingOff++] = l;
    }

    void grow(int newBlockCount) {
        this.minValues = Arrays.copyOf(this.minValues, newBlockCount);
        this.deltas = Arrays.copyOf(this.deltas, newBlockCount);
    }

    abstract void packPendingValues();

    public final long get(long index) {
        if (index < 0L || index >= this.size()) {
            throw new IndexOutOfBoundsException("" + index);
        }
        int block = (int)(index >> this.pageShift);
        int element = (int)(index & (long)this.pageMask);
        return this.get(block, element);
    }

    abstract long get(int var1, int var2);

    abstract Iterator iterator();

    long baseRamBytesUsed() {
        return RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 3 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + 8;
    }

    public long ramBytesUsed() {
        long bytesUsed = RamUsageEstimator.alignObjectSize(this.baseRamBytesUsed()) + 8L + 8L + RamUsageEstimator.sizeOf(this.pending) + RamUsageEstimator.sizeOf(this.minValues) + RamUsageEstimator.alignObjectSize((long)RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + (long)RamUsageEstimator.NUM_BYTES_OBJECT_REF * (long)this.deltas.length);
        return bytesUsed + this.deltasBytes;
    }

    abstract class Iterator {
        long[] currentValues;
        int vOff = 0;
        int pOff = 0;

        Iterator() {
            if (AbstractAppendingLongBuffer.this.valuesOff == 0) {
                this.currentValues = AbstractAppendingLongBuffer.this.pending;
            } else {
                this.currentValues = new long[AbstractAppendingLongBuffer.this.pending.length];
                this.fillValues();
            }
        }

        abstract void fillValues();

        public final boolean hasNext() {
            return this.vOff < AbstractAppendingLongBuffer.this.valuesOff || this.vOff == AbstractAppendingLongBuffer.this.valuesOff && this.pOff < AbstractAppendingLongBuffer.this.pendingOff;
        }

        public final long next() {
            assert (this.hasNext());
            long result = this.currentValues[this.pOff++];
            if (this.pOff == AbstractAppendingLongBuffer.this.pending.length) {
                ++this.vOff;
                this.pOff = 0;
                if (this.vOff <= AbstractAppendingLongBuffer.this.valuesOff) {
                    this.fillValues();
                }
            }
            return result;
        }
    }
}

