/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.LongsRef;
import org.apache.lucene.util.packed.BulkOperation;
import org.apache.lucene.util.packed.PackedInts;

final class PackedReaderIterator
extends PackedInts.ReaderIteratorImpl {
    final int packedIntsVersion;
    final PackedInts.Format format;
    final BulkOperation bulkOperation;
    final byte[] nextBlocks;
    final LongsRef nextValues;
    final int iterations;
    int position;

    PackedReaderIterator(PackedInts.Format format, int packedIntsVersion, int valueCount, int bitsPerValue, DataInput in, int mem) {
        super(valueCount, bitsPerValue, in);
        this.format = format;
        this.packedIntsVersion = packedIntsVersion;
        this.bulkOperation = BulkOperation.of(format, bitsPerValue);
        this.iterations = this.iterations(mem);
        assert (valueCount == 0 || this.iterations > 0);
        this.nextBlocks = new byte[this.iterations * this.bulkOperation.byteBlockCount()];
        this.nextValues = new LongsRef(new long[this.iterations * this.bulkOperation.byteValueCount()], 0, 0);
        this.nextValues.offset = this.nextValues.longs.length;
        this.position = -1;
    }

    private int iterations(int mem) {
        int iterations = this.bulkOperation.computeIterations(this.valueCount, mem);
        if (this.packedIntsVersion < 1) {
            iterations = iterations + 7 & 0xFFFFFFF8;
        }
        return iterations;
    }

    @Override
    public LongsRef next(int count) throws IOException {
        assert (this.nextValues.length >= 0);
        assert (count > 0);
        assert (this.nextValues.offset + this.nextValues.length <= this.nextValues.longs.length);
        this.nextValues.offset += this.nextValues.length;
        int remaining = this.valueCount - this.position - 1;
        if (remaining <= 0) {
            throw new EOFException();
        }
        count = Math.min(remaining, count);
        if (this.nextValues.offset == this.nextValues.longs.length) {
            long remainingBlocks = this.format.byteCount(this.packedIntsVersion, remaining, this.bitsPerValue);
            int blocksToRead = (int)Math.min(remainingBlocks, (long)this.nextBlocks.length);
            this.in.readBytes(this.nextBlocks, 0, blocksToRead);
            if (blocksToRead < this.nextBlocks.length) {
                Arrays.fill(this.nextBlocks, blocksToRead, this.nextBlocks.length, (byte)0);
            }
            this.bulkOperation.decode(this.nextBlocks, 0, this.nextValues.longs, 0, this.iterations);
            this.nextValues.offset = 0;
        }
        this.nextValues.length = Math.min(this.nextValues.longs.length - this.nextValues.offset, count);
        this.position += this.nextValues.length;
        return this.nextValues;
    }

    @Override
    public int ord() {
        return this.position;
    }
}

