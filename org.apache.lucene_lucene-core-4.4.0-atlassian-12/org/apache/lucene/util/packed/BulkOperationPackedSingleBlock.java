/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperation;

final class BulkOperationPackedSingleBlock
extends BulkOperation {
    private static final int BLOCK_COUNT = 1;
    private final int bitsPerValue;
    private final int valueCount;
    private final long mask;

    public BulkOperationPackedSingleBlock(int bitsPerValue) {
        this.bitsPerValue = bitsPerValue;
        this.valueCount = 64 / bitsPerValue;
        this.mask = (1L << bitsPerValue) - 1L;
    }

    @Override
    public final int longBlockCount() {
        return 1;
    }

    @Override
    public final int byteBlockCount() {
        return 8;
    }

    @Override
    public int longValueCount() {
        return this.valueCount;
    }

    @Override
    public final int byteValueCount() {
        return this.valueCount;
    }

    private static long readLong(byte[] blocks, int blocksOffset) {
        return ((long)blocks[blocksOffset++] & 0xFFL) << 56 | ((long)blocks[blocksOffset++] & 0xFFL) << 48 | ((long)blocks[blocksOffset++] & 0xFFL) << 40 | ((long)blocks[blocksOffset++] & 0xFFL) << 32 | ((long)blocks[blocksOffset++] & 0xFFL) << 24 | ((long)blocks[blocksOffset++] & 0xFFL) << 16 | ((long)blocks[blocksOffset++] & 0xFFL) << 8 | (long)blocks[blocksOffset++] & 0xFFL;
    }

    private int decode(long block, long[] values, int valuesOffset) {
        values[valuesOffset++] = block & this.mask;
        for (int j = 1; j < this.valueCount; ++j) {
            values[valuesOffset++] = (block >>>= this.bitsPerValue) & this.mask;
        }
        return valuesOffset;
    }

    private int decode(long block, int[] values, int valuesOffset) {
        values[valuesOffset++] = (int)(block & this.mask);
        for (int j = 1; j < this.valueCount; ++j) {
            values[valuesOffset++] = (int)((block >>>= this.bitsPerValue) & this.mask);
        }
        return valuesOffset;
    }

    private long encode(long[] values, int valuesOffset) {
        long block = values[valuesOffset++];
        for (int j = 1; j < this.valueCount; ++j) {
            block |= values[valuesOffset++] << j * this.bitsPerValue;
        }
        return block;
    }

    private long encode(int[] values, int valuesOffset) {
        long block = (long)values[valuesOffset++] & 0xFFFFFFFFL;
        for (int j = 1; j < this.valueCount; ++j) {
            block |= ((long)values[valuesOffset++] & 0xFFFFFFFFL) << j * this.bitsPerValue;
        }
        return block;
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block = blocks[blocksOffset++];
            valuesOffset = this.decode(block, values, valuesOffset);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block = BulkOperationPackedSingleBlock.readLong(blocks, blocksOffset);
            blocksOffset += 8;
            valuesOffset = this.decode(block, values, valuesOffset);
        }
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        if (this.bitsPerValue > 32) {
            throw new UnsupportedOperationException("Cannot decode " + this.bitsPerValue + "-bits values into an int[]");
        }
        for (int i = 0; i < iterations; ++i) {
            long block = blocks[blocksOffset++];
            valuesOffset = this.decode(block, values, valuesOffset);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        if (this.bitsPerValue > 32) {
            throw new UnsupportedOperationException("Cannot decode " + this.bitsPerValue + "-bits values into an int[]");
        }
        for (int i = 0; i < iterations; ++i) {
            long block = BulkOperationPackedSingleBlock.readLong(blocks, blocksOffset);
            blocksOffset += 8;
            valuesOffset = this.decode(block, values, valuesOffset);
        }
    }

    @Override
    public void encode(long[] values, int valuesOffset, long[] blocks, int blocksOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            blocks[blocksOffset++] = this.encode(values, valuesOffset);
            valuesOffset += this.valueCount;
        }
    }

    @Override
    public void encode(int[] values, int valuesOffset, long[] blocks, int blocksOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            blocks[blocksOffset++] = this.encode(values, valuesOffset);
            valuesOffset += this.valueCount;
        }
    }

    @Override
    public void encode(long[] values, int valuesOffset, byte[] blocks, int blocksOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block = this.encode(values, valuesOffset);
            valuesOffset += this.valueCount;
            blocksOffset = this.writeLong(block, blocks, blocksOffset);
        }
    }

    @Override
    public void encode(int[] values, int valuesOffset, byte[] blocks, int blocksOffset, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            long block = this.encode(values, valuesOffset);
            valuesOffset += this.valueCount;
            blocksOffset = this.writeLong(block, blocks, blocksOffset);
        }
    }
}

