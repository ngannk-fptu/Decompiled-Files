/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util.packed;

import com.atlassian.lucene36.store.DataOutput;
import com.atlassian.lucene36.util.packed.PackedInts;
import java.io.IOException;

class PackedWriter
extends PackedInts.Writer {
    private long pending;
    private int pendingBitPos = 64;
    private final long[] masks;
    private int written = 0;

    public PackedWriter(DataOutput out, int valueCount, int bitsPerValue) throws IOException {
        super(out, valueCount, bitsPerValue);
        this.masks = new long[bitsPerValue - 1];
        long v = 1L;
        for (int i = 0; i < bitsPerValue - 1; ++i) {
            this.masks[i] = (v *= 2L) - 1L;
        }
    }

    public void add(long v) throws IOException {
        assert (v <= PackedInts.maxValue(this.bitsPerValue)) : "v=" + v + " maxValue=" + PackedInts.maxValue(this.bitsPerValue);
        assert (v >= 0L);
        if (this.pendingBitPos >= this.bitsPerValue) {
            this.pending |= v << this.pendingBitPos - this.bitsPerValue;
            if (this.pendingBitPos == this.bitsPerValue) {
                this.out.writeLong(this.pending);
                this.pending = 0L;
                this.pendingBitPos = 64;
            } else {
                this.pendingBitPos -= this.bitsPerValue;
            }
        } else {
            this.pending |= v >> this.bitsPerValue - this.pendingBitPos & this.masks[this.pendingBitPos - 1];
            this.out.writeLong(this.pending);
            this.pendingBitPos = 64 - this.bitsPerValue + this.pendingBitPos;
            this.pending = v << this.pendingBitPos;
        }
        ++this.written;
    }

    public void finish() throws IOException {
        while (this.written < this.valueCount) {
            this.add(0L);
        }
        if (this.pendingBitPos != 64) {
            this.out.writeLong(this.pending);
        }
    }

    public String toString() {
        return "PackedWriter(written " + this.written + "/" + this.valueCount + " with " + this.bitsPerValue + " bits/value)";
    }
}

