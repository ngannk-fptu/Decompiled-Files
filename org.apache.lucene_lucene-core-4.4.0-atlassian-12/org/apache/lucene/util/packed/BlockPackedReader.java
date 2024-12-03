/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.packed.BlockPackedReaderIterator;
import org.apache.lucene.util.packed.PackedInts;

public final class BlockPackedReader {
    private final int blockShift;
    private final int blockMask;
    private final long valueCount;
    private final long[] minValues;
    private final PackedInts.Reader[] subReaders;

    public BlockPackedReader(IndexInput in, int packedIntsVersion, int blockSize, long valueCount, boolean direct) throws IOException {
        this.valueCount = valueCount;
        this.blockShift = PackedInts.checkBlockSize(blockSize, 64, 0x8000000);
        this.blockMask = blockSize - 1;
        int numBlocks = PackedInts.numBlocks(valueCount, blockSize);
        long[] minValues = null;
        this.subReaders = new PackedInts.Reader[numBlocks];
        for (int i = 0; i < numBlocks; ++i) {
            int token = in.readByte() & 0xFF;
            int bitsPerValue = token >>> 1;
            if (bitsPerValue > 64) {
                throw new IOException("Corrupted");
            }
            if ((token & 1) == 0) {
                if (minValues == null) {
                    minValues = new long[numBlocks];
                }
                minValues[i] = BlockPackedReaderIterator.zigZagDecode(1L + BlockPackedReaderIterator.readVLong(in));
            }
            if (bitsPerValue == 0) {
                this.subReaders[i] = new PackedInts.NullReader(blockSize);
                continue;
            }
            int size = (int)Math.min((long)blockSize, valueCount - (long)i * (long)blockSize);
            if (direct) {
                long pointer = in.getFilePointer();
                this.subReaders[i] = PackedInts.getDirectReaderNoHeader(in, PackedInts.Format.PACKED, packedIntsVersion, size, bitsPerValue);
                in.seek(pointer + PackedInts.Format.PACKED.byteCount(packedIntsVersion, size, bitsPerValue));
                continue;
            }
            this.subReaders[i] = PackedInts.getReaderNoHeader(in, PackedInts.Format.PACKED, packedIntsVersion, size, bitsPerValue);
        }
        this.minValues = minValues;
    }

    public long get(long index) {
        assert (index >= 0L && index < this.valueCount);
        int block = (int)(index >>> this.blockShift);
        int idx = (int)(index & (long)this.blockMask);
        return (this.minValues == null ? 0L : this.minValues[block]) + this.subReaders[block].get(idx);
    }
}

