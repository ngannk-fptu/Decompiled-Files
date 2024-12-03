/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.LongsRef;
import org.apache.lucene.util.packed.PackedInts;

public final class BlockPackedReaderIterator {
    DataInput in;
    final int packedIntsVersion;
    long valueCount;
    final int blockSize;
    final long[] values;
    final LongsRef valuesRef;
    byte[] blocks;
    int off;
    long ord;

    static long zigZagDecode(long n) {
        return n >>> 1 ^ -(n & 1L);
    }

    static long readVLong(DataInput in) throws IOException {
        byte b = in.readByte();
        if (b >= 0) {
            return b;
        }
        long i = (long)b & 0x7FL;
        b = in.readByte();
        i |= ((long)b & 0x7FL) << 7;
        if (b >= 0) {
            return i;
        }
        b = in.readByte();
        i |= ((long)b & 0x7FL) << 14;
        if (b >= 0) {
            return i;
        }
        b = in.readByte();
        i |= ((long)b & 0x7FL) << 21;
        if (b >= 0) {
            return i;
        }
        b = in.readByte();
        i |= ((long)b & 0x7FL) << 28;
        if (b >= 0) {
            return i;
        }
        b = in.readByte();
        i |= ((long)b & 0x7FL) << 35;
        if (b >= 0) {
            return i;
        }
        b = in.readByte();
        i |= ((long)b & 0x7FL) << 42;
        if (b >= 0) {
            return i;
        }
        b = in.readByte();
        i |= ((long)b & 0x7FL) << 49;
        if (b >= 0) {
            return i;
        }
        b = in.readByte();
        return i |= ((long)b & 0xFFL) << 56;
    }

    public BlockPackedReaderIterator(DataInput in, int packedIntsVersion, int blockSize, long valueCount) {
        PackedInts.checkBlockSize(blockSize, 64, 0x8000000);
        this.packedIntsVersion = packedIntsVersion;
        this.blockSize = blockSize;
        this.values = new long[blockSize];
        this.valuesRef = new LongsRef(this.values, 0, 0);
        this.reset(in, valueCount);
    }

    public void reset(DataInput in, long valueCount) {
        this.in = in;
        assert (valueCount >= 0L);
        this.valueCount = valueCount;
        this.off = this.blockSize;
        this.ord = 0L;
    }

    public void skip(long count) throws IOException {
        assert (count >= 0L);
        if (this.ord + count > this.valueCount || this.ord + count < 0L) {
            throw new EOFException();
        }
        int skipBuffer = (int)Math.min(count, (long)(this.blockSize - this.off));
        this.off += skipBuffer;
        this.ord += (long)skipBuffer;
        if ((count -= (long)skipBuffer) == 0L) {
            return;
        }
        assert (this.off == this.blockSize);
        while (count >= (long)this.blockSize) {
            int token = this.in.readByte() & 0xFF;
            int bitsPerValue = token >>> 1;
            if (bitsPerValue > 64) {
                throw new IOException("Corrupted");
            }
            if ((token & 1) == 0) {
                BlockPackedReaderIterator.readVLong(this.in);
            }
            long blockBytes = PackedInts.Format.PACKED.byteCount(this.packedIntsVersion, this.blockSize, bitsPerValue);
            this.skipBytes(blockBytes);
            this.ord += (long)this.blockSize;
            count -= (long)this.blockSize;
        }
        if (count == 0L) {
            return;
        }
        assert (count < (long)this.blockSize);
        this.refill();
        this.ord += count;
        this.off = (int)((long)this.off + count);
    }

    private void skipBytes(long count) throws IOException {
        if (this.in instanceof IndexInput) {
            IndexInput iin = (IndexInput)this.in;
            iin.seek(iin.getFilePointer() + count);
        } else {
            int toSkip;
            if (this.blocks == null) {
                this.blocks = new byte[this.blockSize];
            }
            for (long skipped = 0L; skipped < count; skipped += (long)toSkip) {
                toSkip = (int)Math.min((long)this.blocks.length, count - skipped);
                this.in.readBytes(this.blocks, 0, toSkip);
            }
        }
    }

    public long next() throws IOException {
        if (this.ord == this.valueCount) {
            throw new EOFException();
        }
        if (this.off == this.blockSize) {
            this.refill();
        }
        long value = this.values[this.off++];
        ++this.ord;
        return value;
    }

    public LongsRef next(int count) throws IOException {
        assert (count > 0);
        if (this.ord == this.valueCount) {
            throw new EOFException();
        }
        if (this.off == this.blockSize) {
            this.refill();
        }
        count = Math.min(count, this.blockSize - this.off);
        count = (int)Math.min((long)count, this.valueCount - this.ord);
        this.valuesRef.offset = this.off;
        this.valuesRef.length = count;
        this.off += count;
        this.ord += (long)count;
        return this.valuesRef;
    }

    private void refill() throws IOException {
        long minValue;
        int token = this.in.readByte() & 0xFF;
        boolean minEquals0 = (token & 1) != 0;
        int bitsPerValue = token >>> 1;
        if (bitsPerValue > 64) {
            throw new IOException("Corrupted");
        }
        long l = minValue = minEquals0 ? 0L : BlockPackedReaderIterator.zigZagDecode(1L + BlockPackedReaderIterator.readVLong(this.in));
        assert (minEquals0 || minValue != 0L);
        if (bitsPerValue == 0) {
            Arrays.fill(this.values, minValue);
        } else {
            PackedInts.Decoder decoder = PackedInts.getDecoder(PackedInts.Format.PACKED, this.packedIntsVersion, bitsPerValue);
            int iterations = this.blockSize / decoder.byteValueCount();
            int blocksSize = iterations * decoder.byteBlockCount();
            if (this.blocks == null || this.blocks.length < blocksSize) {
                this.blocks = new byte[blocksSize];
            }
            int valueCount = (int)Math.min(this.valueCount - this.ord, (long)this.blockSize);
            int blocksCount = (int)PackedInts.Format.PACKED.byteCount(this.packedIntsVersion, valueCount, bitsPerValue);
            this.in.readBytes(this.blocks, 0, blocksCount);
            decoder.decode(this.blocks, 0, this.values, 0, iterations);
            if (minValue != 0L) {
                int i = 0;
                while (i < valueCount) {
                    int n = i++;
                    this.values[n] = this.values[n] + minValue;
                }
            }
        }
        this.off = 0;
    }

    public long ord() {
        return this.ord;
    }
}

