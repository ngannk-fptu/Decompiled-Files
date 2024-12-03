/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.packed.PackedInts;

public final class PackedDataOutput {
    final DataOutput out;
    long current;
    int remainingBits;

    public PackedDataOutput(DataOutput out) {
        this.out = out;
        this.current = 0L;
        this.remainingBits = 8;
    }

    public void writeLong(long value, int bitsPerValue) throws IOException {
        assert (bitsPerValue == 64 || value >= 0L && value <= PackedInts.maxValue(bitsPerValue));
        while (bitsPerValue > 0) {
            if (this.remainingBits == 0) {
                this.out.writeByte((byte)this.current);
                this.current = 0L;
                this.remainingBits = 8;
            }
            int bits = Math.min(this.remainingBits, bitsPerValue);
            this.current |= (value >>> bitsPerValue - bits & (1L << bits) - 1L) << this.remainingBits - bits;
            bitsPerValue -= bits;
            this.remainingBits -= bits;
        }
    }

    public void flush() throws IOException {
        if (this.remainingBits < 8) {
            this.out.writeByte((byte)this.current);
        }
        this.remainingBits = 8;
        this.current = 0L;
    }
}

