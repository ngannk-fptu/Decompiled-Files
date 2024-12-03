/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.packed.PackedInts;

class DirectPackedReader
extends PackedInts.ReaderImpl {
    private final IndexInput in;
    private final long startPointer;

    public DirectPackedReader(int bitsPerValue, int valueCount, IndexInput in) {
        super(valueCount, bitsPerValue);
        this.in = in;
        this.startPointer = in.getFilePointer();
    }

    @Override
    public long get(int index) {
        long majorBitPos = (long)index * (long)this.bitsPerValue;
        long elementPos = majorBitPos >>> 3;
        try {
            this.in.seek(this.startPointer + elementPos);
            byte b0 = this.in.readByte();
            int bitPos = (int)(majorBitPos & 7L);
            if (bitPos + this.bitsPerValue <= 8) {
                return ((long)b0 & (1L << 8 - bitPos) - 1L) >>> 8 - bitPos - this.bitsPerValue;
            }
            int remainingBits = this.bitsPerValue - 8 + bitPos;
            long result = ((long)b0 & (1L << 8 - bitPos) - 1L) << remainingBits;
            while (remainingBits >= 8) {
                result |= ((long)this.in.readByte() & 0xFFL) << (remainingBits -= 8);
            }
            if (remainingBits > 0) {
                result |= ((long)this.in.readByte() & 0xFFL) >>> 8 - remainingBits;
            }
            return result;
        }
        catch (IOException ioe) {
            throw new IllegalStateException("failed", ioe);
        }
    }

    @Override
    public long ramBytesUsed() {
        return 0L;
    }
}

