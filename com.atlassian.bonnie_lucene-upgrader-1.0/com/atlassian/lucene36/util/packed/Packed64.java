/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util.packed;

import com.atlassian.lucene36.store.DataInput;
import com.atlassian.lucene36.util.RamUsageEstimator;
import com.atlassian.lucene36.util.packed.PackedInts;
import java.io.IOException;
import java.util.Arrays;

class Packed64
extends PackedInts.ReaderImpl
implements PackedInts.Mutable {
    static final int BLOCK_SIZE = 64;
    static final int BLOCK_BITS = 6;
    static final int MOD_MASK = 63;
    private static final int ENTRY_SIZE = 65;
    private static final int FAC_BITPOS = 3;
    private static final int[][] SHIFTS;
    private static final long[][] MASKS;
    private static final long[][] WRITE_MASKS;
    private long[] blocks;
    private int maxPos;
    private int[] shifts;
    private long[] readMasks;
    private long[] writeMasks;

    public Packed64(int valueCount, int bitsPerValue) {
        this(new long[(int)((long)valueCount * (long)bitsPerValue / 64L + 2L)], valueCount, bitsPerValue);
    }

    public Packed64(long[] blocks, int valueCount, int bitsPerValue) {
        super(valueCount, bitsPerValue);
        this.blocks = blocks;
        this.updateCached();
    }

    public Packed64(DataInput in, int valueCount, int bitsPerValue) throws IOException {
        super(valueCount, bitsPerValue);
        int size = Packed64.size(valueCount, bitsPerValue);
        this.blocks = new long[size + 1];
        for (int i = 0; i < size; ++i) {
            this.blocks[i] = in.readLong();
        }
        this.updateCached();
    }

    private static int size(int valueCount, int bitsPerValue) {
        long totBitCount = (long)valueCount * (long)bitsPerValue;
        return (int)(totBitCount / 64L + (long)(totBitCount % 64L == 0L ? 0 : 1));
    }

    private void updateCached() {
        this.readMasks = MASKS[this.bitsPerValue];
        this.shifts = SHIFTS[this.bitsPerValue];
        this.writeMasks = WRITE_MASKS[this.bitsPerValue];
        this.maxPos = (int)((long)this.blocks.length * 64L / (long)this.bitsPerValue - 2L);
    }

    public long get(int index) {
        assert (index >= 0 && index < this.size());
        long majorBitPos = (long)index * (long)this.bitsPerValue;
        int elementPos = (int)(majorBitPos >>> 6);
        int bitPos = (int)(majorBitPos & 0x3FL);
        int base = bitPos * 3;
        assert (elementPos < this.blocks.length) : "elementPos: " + elementPos + "; blocks.len: " + this.blocks.length;
        return this.blocks[elementPos] << this.shifts[base] >>> this.shifts[base + 1] | this.blocks[elementPos + 1] >>> this.shifts[base + 2] & this.readMasks[bitPos];
    }

    public void set(int index, long value) {
        long majorBitPos = (long)index * (long)this.bitsPerValue;
        int elementPos = (int)(majorBitPos >>> 6);
        int bitPos = (int)(majorBitPos & 0x3FL);
        int base = bitPos * 3;
        this.blocks[elementPos] = this.blocks[elementPos] & this.writeMasks[base] | value << this.shifts[base + 1] >>> this.shifts[base];
        this.blocks[elementPos + 1] = this.blocks[elementPos + 1] & this.writeMasks[base + 1] | value << this.shifts[base + 2] & this.writeMasks[base + 2];
    }

    public String toString() {
        return "Packed64(bitsPerValue=" + this.bitsPerValue + ", size=" + this.size() + ", maxPos=" + this.maxPos + ", elements.length=" + this.blocks.length + ")";
    }

    public long ramBytesUsed() {
        return RamUsageEstimator.sizeOf(this.blocks);
    }

    public void clear() {
        Arrays.fill(this.blocks, 0L);
    }

    static {
        int elementBits;
        SHIFTS = new int[65][195];
        MASKS = new long[65][65];
        for (elementBits = 1; elementBits <= 64; ++elementBits) {
            for (int bitPos = 0; bitPos < 64; ++bitPos) {
                int[] currentShifts = SHIFTS[elementBits];
                int base = bitPos * 3;
                currentShifts[base] = bitPos;
                currentShifts[base + 1] = 64 - elementBits;
                if (bitPos <= 64 - elementBits) {
                    currentShifts[base + 2] = 0;
                    Packed64.MASKS[elementBits][bitPos] = 0L;
                    continue;
                }
                int rBits = elementBits - (64 - bitPos);
                currentShifts[base + 2] = 64 - rBits;
                Packed64.MASKS[elementBits][bitPos] = -1L << rBits ^ 0xFFFFFFFFFFFFFFFFL;
            }
        }
        WRITE_MASKS = new long[65][195];
        for (elementBits = 1; elementBits <= 64; ++elementBits) {
            long elementPosMask = -1L << elementBits ^ 0xFFFFFFFFFFFFFFFFL;
            int[] currentShifts = SHIFTS[elementBits];
            long[] currentMasks = WRITE_MASKS[elementBits];
            for (int bitPos = 0; bitPos < 64; ++bitPos) {
                int base = bitPos * 3;
                currentMasks[base] = elementPosMask << currentShifts[base + 1] >>> currentShifts[base] ^ 0xFFFFFFFFFFFFFFFFL;
                if (bitPos <= 64 - elementBits) {
                    currentMasks[base + 1] = -1L;
                    currentMasks[base + 2] = 0L;
                    continue;
                }
                currentMasks[base + 1] = elementPosMask << currentShifts[base + 2] ^ 0xFFFFFFFFFFFFFFFFL;
                currentMasks[base + 2] = currentShifts[base + 2] == 0 ? 0L : -1L;
            }
        }
    }
}

