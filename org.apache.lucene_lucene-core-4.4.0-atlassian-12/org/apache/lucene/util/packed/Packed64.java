/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.packed.BulkOperation;
import org.apache.lucene.util.packed.PackedInts;

class Packed64
extends PackedInts.MutableImpl {
    static final int BLOCK_SIZE = 64;
    static final int BLOCK_BITS = 6;
    static final int MOD_MASK = 63;
    private final long[] blocks;
    private final long maskRight;
    private final int bpvMinusBlockSize;

    public Packed64(int valueCount, int bitsPerValue) {
        super(valueCount, bitsPerValue);
        PackedInts.Format format = PackedInts.Format.PACKED;
        int longCount = format.longCount(1, valueCount, bitsPerValue);
        this.blocks = new long[longCount];
        this.maskRight = -1L << 64 - bitsPerValue >>> 64 - bitsPerValue;
        this.bpvMinusBlockSize = bitsPerValue - 64;
    }

    public Packed64(int packedIntsVersion, DataInput in, int valueCount, int bitsPerValue) throws IOException {
        super(valueCount, bitsPerValue);
        PackedInts.Format format = PackedInts.Format.PACKED;
        long byteCount = format.byteCount(packedIntsVersion, valueCount, bitsPerValue);
        int longCount = format.longCount(1, valueCount, bitsPerValue);
        this.blocks = new long[longCount];
        int i = 0;
        while ((long)i < byteCount / 8L) {
            this.blocks[i] = in.readLong();
            ++i;
        }
        int remaining = (int)(byteCount % 8L);
        if (remaining != 0) {
            long lastLong = 0L;
            for (int i2 = 0; i2 < remaining; ++i2) {
                lastLong |= ((long)in.readByte() & 0xFFL) << 56 - i2 * 8;
            }
            this.blocks[this.blocks.length - 1] = lastLong;
        }
        this.maskRight = -1L << 64 - bitsPerValue >>> 64 - bitsPerValue;
        this.bpvMinusBlockSize = bitsPerValue - 64;
    }

    @Override
    public long get(int index) {
        long majorBitPos = (long)index * (long)this.bitsPerValue;
        int elementPos = (int)(majorBitPos >>> 6);
        long endBits = (majorBitPos & 0x3FL) + (long)this.bpvMinusBlockSize;
        if (endBits <= 0L) {
            return this.blocks[elementPos] >>> (int)(-endBits) & this.maskRight;
        }
        return (this.blocks[elementPos] << (int)endBits | this.blocks[elementPos + 1] >>> (int)(64L - endBits)) & this.maskRight;
    }

    @Override
    public int get(int index, long[] arr, int off, int len) {
        assert (len > 0) : "len must be > 0 (got " + len + ")";
        assert (index >= 0 && index < this.valueCount);
        len = Math.min(len, this.valueCount - index);
        assert (off + len <= arr.length);
        int originalIndex = index;
        BulkOperation decoder = BulkOperation.of(PackedInts.Format.PACKED, this.bitsPerValue);
        int offsetInBlocks = index % decoder.longValueCount();
        if (offsetInBlocks != 0) {
            for (int i = offsetInBlocks; i < decoder.longValueCount() && len > 0; --len, ++i) {
                arr[off++] = this.get(index++);
            }
            if (len == 0) {
                return index - originalIndex;
            }
        }
        assert (index % decoder.longValueCount() == 0);
        int blockIndex = (int)((long)index * (long)this.bitsPerValue >>> 6);
        assert (((long)index * (long)this.bitsPerValue & 0x3FL) == 0L);
        int iterations = len / decoder.longValueCount();
        decoder.decode(this.blocks, blockIndex, arr, off, iterations);
        int gotValues = iterations * decoder.longValueCount();
        index += gotValues;
        assert ((len -= gotValues) >= 0);
        if (index > originalIndex) {
            return index - originalIndex;
        }
        assert (index == originalIndex);
        return super.get(index, arr, off, len);
    }

    @Override
    public void set(int index, long value) {
        long majorBitPos = (long)index * (long)this.bitsPerValue;
        int elementPos = (int)(majorBitPos >>> 6);
        long endBits = (majorBitPos & 0x3FL) + (long)this.bpvMinusBlockSize;
        if (endBits <= 0L) {
            this.blocks[elementPos] = this.blocks[elementPos] & (this.maskRight << (int)(-endBits) ^ 0xFFFFFFFFFFFFFFFFL) | value << (int)(-endBits);
            return;
        }
        this.blocks[elementPos] = this.blocks[elementPos] & (this.maskRight >>> (int)endBits ^ 0xFFFFFFFFFFFFFFFFL) | value >>> (int)endBits;
        this.blocks[elementPos + 1] = this.blocks[elementPos + 1] & -1L >>> (int)endBits | value << (int)(64L - endBits);
    }

    @Override
    public int set(int index, long[] arr, int off, int len) {
        assert (len > 0) : "len must be > 0 (got " + len + ")";
        assert (index >= 0 && index < this.valueCount);
        len = Math.min(len, this.valueCount - index);
        assert (off + len <= arr.length);
        int originalIndex = index;
        BulkOperation encoder = BulkOperation.of(PackedInts.Format.PACKED, this.bitsPerValue);
        int offsetInBlocks = index % encoder.longValueCount();
        if (offsetInBlocks != 0) {
            for (int i = offsetInBlocks; i < encoder.longValueCount() && len > 0; --len, ++i) {
                this.set(index++, arr[off++]);
            }
            if (len == 0) {
                return index - originalIndex;
            }
        }
        assert (index % encoder.longValueCount() == 0);
        int blockIndex = (int)((long)index * (long)this.bitsPerValue >>> 6);
        assert (((long)index * (long)this.bitsPerValue & 0x3FL) == 0L);
        int iterations = len / encoder.longValueCount();
        encoder.encode(arr, off, this.blocks, blockIndex, iterations);
        int setValues = iterations * encoder.longValueCount();
        index += setValues;
        assert ((len -= setValues) >= 0);
        if (index > originalIndex) {
            return index - originalIndex;
        }
        assert (index == originalIndex);
        return super.set(index, arr, off, len);
    }

    public String toString() {
        return "Packed64(bitsPerValue=" + this.bitsPerValue + ", size=" + this.size() + ", elements.length=" + this.blocks.length + ")";
    }

    @Override
    public long ramBytesUsed() {
        return RamUsageEstimator.alignObjectSize(RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 12 + 8 + RamUsageEstimator.NUM_BYTES_OBJECT_REF) + RamUsageEstimator.sizeOf(this.blocks);
    }

    @Override
    public void fill(int fromIndex, int toIndex, long val) {
        assert (PackedInts.bitsRequired(val) <= this.getBitsPerValue());
        assert (fromIndex <= toIndex);
        int span = toIndex - fromIndex;
        int nAlignedValues = 64 / Packed64.gcd(64, this.bitsPerValue);
        if (span <= 3 * nAlignedValues) {
            super.fill(fromIndex, toIndex, val);
            return;
        }
        int fromIndexModNAlignedValues = fromIndex % nAlignedValues;
        if (fromIndexModNAlignedValues != 0) {
            for (int i = fromIndexModNAlignedValues; i < nAlignedValues; ++i) {
                this.set(fromIndex++, val);
            }
        }
        assert (fromIndex % nAlignedValues == 0);
        int nAlignedBlocks = nAlignedValues * this.bitsPerValue >> 6;
        Packed64 values = new Packed64(nAlignedValues, this.bitsPerValue);
        for (int i = 0; i < nAlignedValues; ++i) {
            values.set(i, val);
        }
        long[] nAlignedValuesBlocks = values.blocks;
        assert (nAlignedBlocks <= nAlignedValuesBlocks.length);
        int startBlock = (int)((long)fromIndex * (long)this.bitsPerValue >>> 6);
        int endBlock = (int)((long)toIndex * (long)this.bitsPerValue >>> 6);
        for (int block = startBlock; block < endBlock; ++block) {
            long blockValue;
            this.blocks[block] = blockValue = nAlignedValuesBlocks[block % nAlignedBlocks];
        }
        for (int i = (int)(((long)endBlock << 6) / (long)this.bitsPerValue); i < toIndex; ++i) {
            this.set(i, val);
        }
    }

    private static int gcd(int a, int b) {
        if (a < b) {
            return Packed64.gcd(b, a);
        }
        if (b == 0) {
            return a;
        }
        return Packed64.gcd(b, a % b);
    }

    @Override
    public void clear() {
        Arrays.fill(this.blocks, 0L);
    }
}

