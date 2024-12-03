/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.util.Arrays;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.packed.AbstractAppendingLongBuffer;
import org.apache.lucene.util.packed.PackedInts;

public final class MonotonicAppendingLongBuffer
extends AbstractAppendingLongBuffer {
    float[] averages;

    static long zigZagDecode(long n) {
        return n >>> 1 ^ -(n & 1L);
    }

    static long zigZagEncode(long n) {
        return n >> 63 ^ n << 1;
    }

    public MonotonicAppendingLongBuffer(int initialPageCount, int pageSize) {
        super(initialPageCount, pageSize);
        this.averages = new float[this.pending.length];
    }

    public MonotonicAppendingLongBuffer() {
        this(16, 1024);
    }

    @Override
    long get(int block, int element) {
        if (block == this.valuesOff) {
            return this.pending[element];
        }
        long base = this.minValues[block] + (long)(this.averages[block] * (float)element);
        if (this.deltas[block] == null) {
            return base;
        }
        return base + MonotonicAppendingLongBuffer.zigZagDecode(this.deltas[block].get(element));
    }

    @Override
    void grow(int newBlockCount) {
        super.grow(newBlockCount);
        this.averages = Arrays.copyOf(this.averages, newBlockCount);
    }

    @Override
    void packPendingValues() {
        assert (this.pendingOff == this.pending.length);
        this.minValues[this.valuesOff] = this.pending[0];
        this.averages[this.valuesOff] = (float)(this.pending[this.pending.length - 1] - this.pending[0]) / (float)(this.pending.length - 1);
        for (int i = 0; i < this.pending.length; ++i) {
            this.pending[i] = MonotonicAppendingLongBuffer.zigZagEncode(this.pending[i] - this.minValues[this.valuesOff] - (long)(this.averages[this.valuesOff] * (float)i));
        }
        long maxDelta = 0L;
        for (int i = 0; i < this.pending.length; ++i) {
            if (this.pending[i] < 0L) {
                maxDelta = -1L;
                break;
            }
            maxDelta = Math.max(maxDelta, this.pending[i]);
        }
        if (maxDelta != 0L) {
            int bitsRequired = maxDelta < 0L ? 64 : PackedInts.bitsRequired(maxDelta);
            PackedInts.Mutable mutable = PackedInts.getMutable(this.pendingOff, bitsRequired, 0.0f);
            for (int i = 0; i < this.pendingOff; i += mutable.set(i, this.pending, i, this.pendingOff - i)) {
            }
            this.deltas[this.valuesOff] = mutable;
        }
    }

    @Override
    public Iterator iterator() {
        return new Iterator();
    }

    @Override
    long baseRamBytesUsed() {
        return super.baseRamBytesUsed() + (long)RamUsageEstimator.NUM_BYTES_OBJECT_REF;
    }

    @Override
    public long ramBytesUsed() {
        return super.ramBytesUsed() + RamUsageEstimator.sizeOf(this.averages);
    }

    public final class Iterator
    extends AbstractAppendingLongBuffer.Iterator {
        Iterator() {
            super(MonotonicAppendingLongBuffer.this);
        }

        @Override
        void fillValues() {
            if (this.vOff == MonotonicAppendingLongBuffer.this.valuesOff) {
                this.currentValues = MonotonicAppendingLongBuffer.this.pending;
            } else if (MonotonicAppendingLongBuffer.this.deltas[this.vOff] == null) {
                for (int k = 0; k < MonotonicAppendingLongBuffer.this.pending.length; ++k) {
                    this.currentValues[k] = MonotonicAppendingLongBuffer.this.minValues[this.vOff] + (long)(MonotonicAppendingLongBuffer.this.averages[this.vOff] * (float)k);
                }
            } else {
                int k;
                for (k = 0; k < MonotonicAppendingLongBuffer.this.pending.length; k += MonotonicAppendingLongBuffer.this.deltas[this.vOff].get(k, this.currentValues, k, MonotonicAppendingLongBuffer.this.pending.length - k)) {
                }
                for (k = 0; k < MonotonicAppendingLongBuffer.this.pending.length; ++k) {
                    this.currentValues[k] = MonotonicAppendingLongBuffer.this.minValues[this.vOff] + (long)(MonotonicAppendingLongBuffer.this.averages[this.vOff] * (float)k) + MonotonicAppendingLongBuffer.zigZagDecode(this.currentValues[k]);
                }
            }
        }
    }
}

