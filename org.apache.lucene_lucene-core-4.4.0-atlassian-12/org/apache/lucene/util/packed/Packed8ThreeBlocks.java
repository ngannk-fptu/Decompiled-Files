/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.packed.PackedInts;

final class Packed8ThreeBlocks
extends PackedInts.MutableImpl {
    final byte[] blocks;
    public static final int MAX_SIZE = 0x2AAAAAAA;

    Packed8ThreeBlocks(int valueCount) {
        super(valueCount, 24);
        if (valueCount > 0x2AAAAAAA) {
            throw new ArrayIndexOutOfBoundsException("MAX_SIZE exceeded");
        }
        this.blocks = new byte[valueCount * 3];
    }

    Packed8ThreeBlocks(int packedIntsVersion, DataInput in, int valueCount) throws IOException {
        this(valueCount);
        in.readBytes(this.blocks, 0, 3 * valueCount);
        int remaining = (int)(PackedInts.Format.PACKED.byteCount(packedIntsVersion, valueCount, 24) - 3L * (long)valueCount * 1L);
        for (int i = 0; i < remaining; ++i) {
            in.readByte();
        }
    }

    @Override
    public long get(int index) {
        int o = index * 3;
        return ((long)this.blocks[o] & 0xFFL) << 16 | ((long)this.blocks[o + 1] & 0xFFL) << 8 | (long)this.blocks[o + 2] & 0xFFL;
    }

    @Override
    public int get(int index, long[] arr, int off, int len) {
        assert (len > 0) : "len must be > 0 (got " + len + ")";
        assert (index >= 0 && index < this.valueCount);
        assert (off + len <= arr.length);
        int gets = Math.min(this.valueCount - index, len);
        int end = (index + gets) * 3;
        for (int i = index * 3; i < end; i += 3) {
            arr[off++] = ((long)this.blocks[i] & 0xFFL) << 16 | ((long)this.blocks[i + 1] & 0xFFL) << 8 | (long)this.blocks[i + 2] & 0xFFL;
        }
        return gets;
    }

    @Override
    public void set(int index, long value) {
        int o = index * 3;
        this.blocks[o] = (byte)(value >>> 16);
        this.blocks[o + 1] = (byte)(value >>> 8);
        this.blocks[o + 2] = (byte)value;
    }

    @Override
    public int set(int index, long[] arr, int off, int len) {
        assert (len > 0) : "len must be > 0 (got " + len + ")";
        assert (index >= 0 && index < this.valueCount);
        assert (off + len <= arr.length);
        int sets = Math.min(this.valueCount - index, len);
        int o = index * 3;
        int end = off + sets;
        for (int i = off; i < end; ++i) {
            long value = arr[i];
            this.blocks[o++] = (byte)(value >>> 16);
            this.blocks[o++] = (byte)(value >>> 8);
            this.blocks[o++] = (byte)value;
        }
        return sets;
    }

    @Override
    public void fill(int fromIndex, int toIndex, long val) {
        byte block1 = (byte)(val >>> 16);
        byte block2 = (byte)(val >>> 8);
        byte block3 = (byte)val;
        int end = toIndex * 3;
        for (int i = fromIndex * 3; i < end; i += 3) {
            this.blocks[i] = block1;
            this.blocks[i + 1] = block2;
            this.blocks[i + 2] = block3;
        }
    }

    @Override
    public void clear() {
        Arrays.fill(this.blocks, (byte)0);
    }

    @Override
    public long ramBytesUsed() {
        return RamUsageEstimator.alignObjectSize(RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 8 + RamUsageEstimator.NUM_BYTES_OBJECT_REF) + RamUsageEstimator.sizeOf(this.blocks);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(bitsPerValue=" + this.bitsPerValue + ", size=" + this.size() + ", elements.length=" + this.blocks.length + ")";
    }
}

