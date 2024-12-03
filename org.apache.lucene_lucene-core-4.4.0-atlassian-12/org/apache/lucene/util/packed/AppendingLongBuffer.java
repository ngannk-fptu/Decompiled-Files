/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.util.Arrays;
import org.apache.lucene.util.packed.AbstractAppendingLongBuffer;
import org.apache.lucene.util.packed.PackedInts;

public final class AppendingLongBuffer
extends AbstractAppendingLongBuffer {
    public AppendingLongBuffer(int initialPageCount, int pageSize) {
        super(initialPageCount, pageSize);
    }

    public AppendingLongBuffer() {
        this(16, 1024);
    }

    @Override
    long get(int block, int element) {
        if (block == this.valuesOff) {
            return this.pending[element];
        }
        if (this.deltas[block] == null) {
            return this.minValues[block];
        }
        return this.minValues[block] + this.deltas[block].get(element);
    }

    @Override
    void packPendingValues() {
        assert (this.pendingOff == this.pending.length);
        long minValue = this.pending[0];
        long maxValue = this.pending[0];
        for (int i = 1; i < this.pendingOff; ++i) {
            minValue = Math.min(minValue, this.pending[i]);
            maxValue = Math.max(maxValue, this.pending[i]);
        }
        long delta = maxValue - minValue;
        this.minValues[this.valuesOff] = minValue;
        if (delta != 0L) {
            int bitsRequired = delta < 0L ? 64 : PackedInts.bitsRequired(delta);
            int i = 0;
            while (i < this.pendingOff) {
                int n = i++;
                this.pending[n] = this.pending[n] - minValue;
            }
            PackedInts.Mutable mutable = PackedInts.getMutable(this.pendingOff, bitsRequired, 0.0f);
            for (int i2 = 0; i2 < this.pendingOff; i2 += mutable.set(i2, this.pending, i2, this.pendingOff - i2)) {
            }
            this.deltas[this.valuesOff] = mutable;
        }
    }

    @Override
    public Iterator iterator() {
        return new Iterator();
    }

    public final class Iterator
    extends AbstractAppendingLongBuffer.Iterator {
        Iterator() {
        }

        @Override
        void fillValues() {
            if (this.vOff == AppendingLongBuffer.this.valuesOff) {
                this.currentValues = AppendingLongBuffer.this.pending;
            } else if (AppendingLongBuffer.this.deltas[this.vOff] == null) {
                Arrays.fill(this.currentValues, AppendingLongBuffer.this.minValues[this.vOff]);
            } else {
                int k;
                for (k = 0; k < AppendingLongBuffer.this.pending.length; k += AppendingLongBuffer.this.deltas[this.vOff].get(k, this.currentValues, k, AppendingLongBuffer.this.pending.length - k)) {
                }
                k = 0;
                while (k < AppendingLongBuffer.this.pending.length) {
                    int n = k++;
                    this.currentValues[n] = this.currentValues[n] + AppendingLongBuffer.this.minValues[this.vOff];
                }
            }
        }
    }
}

