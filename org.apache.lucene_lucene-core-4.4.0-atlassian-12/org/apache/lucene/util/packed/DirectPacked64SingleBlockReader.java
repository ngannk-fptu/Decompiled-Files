/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.packed.PackedInts;

final class DirectPacked64SingleBlockReader
extends PackedInts.ReaderImpl {
    private final IndexInput in;
    private final long startPointer;
    private final int valuesPerBlock;
    private final long mask;

    DirectPacked64SingleBlockReader(int bitsPerValue, int valueCount, IndexInput in) {
        super(valueCount, bitsPerValue);
        this.in = in;
        this.startPointer = in.getFilePointer();
        this.valuesPerBlock = 64 / bitsPerValue;
        this.mask = -1L << bitsPerValue ^ 0xFFFFFFFFFFFFFFFFL;
    }

    @Override
    public long get(int index) {
        int blockOffset = index / this.valuesPerBlock;
        long skip = (long)blockOffset << 3;
        try {
            this.in.seek(this.startPointer + skip);
            long block = this.in.readLong();
            int offsetInBlock = index % this.valuesPerBlock;
            return block >>> offsetInBlock * this.bitsPerValue & this.mask;
        }
        catch (IOException e) {
            throw new IllegalStateException("failed", e);
        }
    }

    @Override
    public long ramBytesUsed() {
        return 0L;
    }
}

