/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import org.apache.lucene.util.packed.BulkOperation;
import org.apache.lucene.util.packed.PackedInts;

class BulkOperationPacked
extends BulkOperation {
    private final int bitsPerValue;
    private final int longBlockCount;
    private final int longValueCount;
    private final int byteBlockCount;
    private final int byteValueCount;
    private final long mask;
    private final int intMask;

    public BulkOperationPacked(int bitsPerValue) {
        this.bitsPerValue = bitsPerValue;
        assert (bitsPerValue > 0 && bitsPerValue <= 64);
        int blocks = bitsPerValue;
        while ((blocks & 1) == 0) {
            blocks >>>= 1;
        }
        this.longBlockCount = blocks;
        this.longValueCount = 64 * this.longBlockCount / bitsPerValue;
        int byteBlockCount = 8 * this.longBlockCount;
        int byteValueCount = this.longValueCount;
        while ((byteBlockCount & 1) == 0 && (byteValueCount & 1) == 0) {
            byteBlockCount >>>= 1;
            byteValueCount >>>= 1;
        }
        this.byteBlockCount = byteBlockCount;
        this.byteValueCount = byteValueCount;
        this.mask = bitsPerValue == 64 ? -1L : (1L << bitsPerValue) - 1L;
        this.intMask = (int)this.mask;
        assert (this.longValueCount * bitsPerValue == 64 * this.longBlockCount);
    }

    @Override
    public int longBlockCount() {
        return this.longBlockCount;
    }

    @Override
    public int longValueCount() {
        return this.longValueCount;
    }

    @Override
    public int byteBlockCount() {
        return this.byteBlockCount;
    }

    @Override
    public int byteValueCount() {
        return this.byteValueCount;
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        int bitsLeft = 64;
        for (int i = 0; i < this.longValueCount * iterations; ++i) {
            if ((bitsLeft -= this.bitsPerValue) < 0) {
                values[valuesOffset++] = (blocks[blocksOffset++] & (1L << this.bitsPerValue + bitsLeft) - 1L) << -bitsLeft | blocks[blocksOffset] >>> 64 + bitsLeft;
                bitsLeft += 64;
                continue;
            }
            values[valuesOffset++] = blocks[blocksOffset] >>> bitsLeft & this.mask;
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {
        long nextValue = 0L;
        int bitsLeft = this.bitsPerValue;
        for (int i = 0; i < iterations * this.byteBlockCount; ++i) {
            long bytes = (long)blocks[blocksOffset++] & 0xFFL;
            if (bitsLeft > 8) {
                nextValue |= bytes << (bitsLeft -= 8);
                continue;
            }
            int bits = 8 - bitsLeft;
            values[valuesOffset++] = nextValue | bytes >>> bits;
            while (bits >= this.bitsPerValue) {
                values[valuesOffset++] = bytes >>> (bits -= this.bitsPerValue) & this.mask;
            }
            bitsLeft = this.bitsPerValue - bits;
            nextValue = (bytes & (1L << bits) - 1L) << bitsLeft;
        }
        assert (bitsLeft == this.bitsPerValue);
    }

    @Override
    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        if (this.bitsPerValue > 32) {
            throw new UnsupportedOperationException("Cannot decode " + this.bitsPerValue + "-bits values into an int[]");
        }
        int bitsLeft = 64;
        for (int i = 0; i < this.longValueCount * iterations; ++i) {
            if ((bitsLeft -= this.bitsPerValue) < 0) {
                values[valuesOffset++] = (int)((blocks[blocksOffset++] & (1L << this.bitsPerValue + bitsLeft) - 1L) << -bitsLeft | blocks[blocksOffset] >>> 64 + bitsLeft);
                bitsLeft += 64;
                continue;
            }
            values[valuesOffset++] = (int)(blocks[blocksOffset] >>> bitsLeft & this.mask);
        }
    }

    @Override
    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {
        int nextValue = 0;
        int bitsLeft = this.bitsPerValue;
        for (int i = 0; i < iterations * this.byteBlockCount; ++i) {
            int bytes = blocks[blocksOffset++] & 0xFF;
            if (bitsLeft > 8) {
                nextValue |= bytes << (bitsLeft -= 8);
                continue;
            }
            int bits = 8 - bitsLeft;
            values[valuesOffset++] = nextValue | bytes >>> bits;
            while (bits >= this.bitsPerValue) {
                values[valuesOffset++] = bytes >>> (bits -= this.bitsPerValue) & this.intMask;
            }
            bitsLeft = this.bitsPerValue - bits;
            nextValue = (bytes & (1 << bits) - 1) << bitsLeft;
        }
        assert (bitsLeft == this.bitsPerValue);
    }

    @Override
    public void encode(long[] values, int valuesOffset, long[] blocks, int blocksOffset, int iterations) {
        long nextBlock = 0L;
        int bitsLeft = 64;
        for (int i = 0; i < this.longValueCount * iterations; ++i) {
            if ((bitsLeft -= this.bitsPerValue) > 0) {
                nextBlock |= values[valuesOffset++] << bitsLeft;
                continue;
            }
            if (bitsLeft == 0) {
                blocks[blocksOffset++] = nextBlock |= values[valuesOffset++];
                nextBlock = 0L;
                bitsLeft = 64;
                continue;
            }
            blocks[blocksOffset++] = nextBlock |= values[valuesOffset] >>> -bitsLeft;
            nextBlock = (values[valuesOffset++] & (1L << -bitsLeft) - 1L) << 64 + bitsLeft;
            bitsLeft += 64;
        }
    }

    @Override
    public void encode(int[] values, int valuesOffset, long[] blocks, int blocksOffset, int iterations) {
        long nextBlock = 0L;
        int bitsLeft = 64;
        for (int i = 0; i < this.longValueCount * iterations; ++i) {
            if ((bitsLeft -= this.bitsPerValue) > 0) {
                nextBlock |= ((long)values[valuesOffset++] & 0xFFFFFFFFL) << bitsLeft;
                continue;
            }
            if (bitsLeft == 0) {
                blocks[blocksOffset++] = nextBlock |= (long)values[valuesOffset++] & 0xFFFFFFFFL;
                nextBlock = 0L;
                bitsLeft = 64;
                continue;
            }
            blocks[blocksOffset++] = nextBlock |= ((long)values[valuesOffset] & 0xFFFFFFFFL) >>> -bitsLeft;
            nextBlock = ((long)values[valuesOffset++] & (1L << -bitsLeft) - 1L) << 64 + bitsLeft;
            bitsLeft += 64;
        }
    }

    @Override
    public void encode(long[] values, int valuesOffset, byte[] blocks, int blocksOffset, int iterations) {
        int nextBlock = 0;
        int bitsLeft = 8;
        for (int i = 0; i < this.byteValueCount * iterations; ++i) {
            long v = values[valuesOffset++];
            assert (this.bitsPerValue == 64 || PackedInts.bitsRequired(v) <= this.bitsPerValue);
            if (this.bitsPerValue < bitsLeft) {
                nextBlock = (int)((long)nextBlock | v << bitsLeft - this.bitsPerValue);
                bitsLeft -= this.bitsPerValue;
                continue;
            }
            int bits = this.bitsPerValue - bitsLeft;
            blocks[blocksOffset++] = (byte)((long)nextBlock | v >>> bits);
            while (bits >= 8) {
                blocks[blocksOffset++] = (byte)(v >>> (bits -= 8));
            }
            bitsLeft = 8 - bits;
            nextBlock = (int)((v & (1L << bits) - 1L) << bitsLeft);
        }
        assert (bitsLeft == 8);
    }

    @Override
    public void encode(int[] values, int valuesOffset, byte[] blocks, int blocksOffset, int iterations) {
        int nextBlock = 0;
        int bitsLeft = 8;
        for (int i = 0; i < this.byteValueCount * iterations; ++i) {
            int v = values[valuesOffset++];
            assert (PackedInts.bitsRequired((long)v & 0xFFFFFFFFL) <= this.bitsPerValue);
            if (this.bitsPerValue < bitsLeft) {
                nextBlock |= v << bitsLeft - this.bitsPerValue;
                bitsLeft -= this.bitsPerValue;
                continue;
            }
            int bits = this.bitsPerValue - bitsLeft;
            blocks[blocksOffset++] = (byte)(nextBlock | v >>> bits);
            while (bits >= 8) {
                blocks[blocksOffset++] = (byte)(v >>> (bits -= 8));
            }
            bitsLeft = 8 - bits;
            nextBlock = (v & (1 << bits) - 1) << bitsLeft;
        }
        assert (bitsLeft == 8);
    }
}

