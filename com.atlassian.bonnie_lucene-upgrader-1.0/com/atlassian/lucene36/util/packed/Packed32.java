/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util.packed;

import com.atlassian.lucene36.store.DataInput;
import com.atlassian.lucene36.util.RamUsageEstimator;
import com.atlassian.lucene36.util.packed.PackedInts;
import java.io.IOException;
import java.util.Arrays;

class Packed32
extends PackedInts.ReaderImpl
implements PackedInts.Mutable {
    static final int BLOCK_SIZE = 32;
    static final int BLOCK_BITS = 5;
    static final int MOD_MASK = 31;
    private static final int ENTRY_SIZE = 33;
    private static final int FAC_BITPOS = 3;
    private static final int[][] SHIFTS;
    private static final int[][] MASKS;
    private static final int[][] WRITE_MASKS;
    private int[] blocks;
    private int maxPos;
    private int[] shifts;
    private int[] readMasks;
    private int[] writeMasks;

    public Packed32(int valueCount, int bitsPerValue) {
        this(new int[(int)((long)valueCount * (long)bitsPerValue / 32L + 2L)], valueCount, bitsPerValue);
    }

    public Packed32(DataInput in, int valueCount, int bitsPerValue) throws IOException {
        super(valueCount, bitsPerValue);
        int size = Packed32.size(bitsPerValue, valueCount);
        this.blocks = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            this.blocks[i] = in.readInt();
        }
        if (size % 2 == 1) {
            in.readInt();
        }
        this.updateCached();
    }

    private static int size(int bitsPerValue, int valueCount) {
        long totBitCount = (long)valueCount * (long)bitsPerValue;
        return (int)(totBitCount / 32L + (long)(totBitCount % 32L == 0L ? 0 : 1));
    }

    public Packed32(int[] blocks, int valueCount, int bitsPerValue) {
        super(valueCount, bitsPerValue);
        if (bitsPerValue > 31) {
            throw new IllegalArgumentException(String.format("This array only supports values of 31 bits or less. The required number of bits was %d. The Packed64 implementation allows values with more than 31 bits", bitsPerValue));
        }
        this.blocks = blocks;
        this.updateCached();
    }

    private void updateCached() {
        this.readMasks = MASKS[this.bitsPerValue];
        this.maxPos = (int)((long)this.blocks.length * 32L / (long)this.bitsPerValue - 2L);
        this.shifts = SHIFTS[this.bitsPerValue];
        this.writeMasks = WRITE_MASKS[this.bitsPerValue];
    }

    public long get(int index) {
        assert (index >= 0 && index < this.size());
        long majorBitPos = (long)index * (long)this.bitsPerValue;
        int elementPos = (int)(majorBitPos >>> 5);
        int bitPos = (int)(majorBitPos & 0x1FL);
        int base = bitPos * 3;
        return this.blocks[elementPos] << this.shifts[base] >>> this.shifts[base + 1] | this.blocks[elementPos + 1] >>> this.shifts[base + 2] & this.readMasks[bitPos];
    }

    public void set(int index, long value) {
        int intValue = (int)value;
        long majorBitPos = (long)index * (long)this.bitsPerValue;
        int elementPos = (int)(majorBitPos >>> 5);
        int bitPos = (int)(majorBitPos & 0x1FL);
        int base = bitPos * 3;
        this.blocks[elementPos] = this.blocks[elementPos] & this.writeMasks[base] | intValue << this.shifts[base + 1] >>> this.shifts[base];
        this.blocks[elementPos + 1] = this.blocks[elementPos + 1] & this.writeMasks[base + 1] | intValue << this.shifts[base + 2] & this.writeMasks[base + 2];
    }

    public void clear() {
        Arrays.fill(this.blocks, 0);
    }

    public String toString() {
        return "Packed32(bitsPerValue=" + this.bitsPerValue + ", maxPos=" + this.maxPos + ", elements.length=" + this.blocks.length + ")";
    }

    public long ramBytesUsed() {
        return RamUsageEstimator.sizeOf(this.blocks);
    }

    static {
        int[] currentShifts;
        int elementBits;
        SHIFTS = new int[33][99];
        MASKS = new int[33][33];
        for (elementBits = 1; elementBits <= 32; ++elementBits) {
            for (int bitPos = 0; bitPos < 32; ++bitPos) {
                currentShifts = SHIFTS[elementBits];
                int base = bitPos * 3;
                currentShifts[base] = bitPos;
                currentShifts[base + 1] = 32 - elementBits;
                if (bitPos <= 32 - elementBits) {
                    currentShifts[base + 2] = 0;
                    Packed32.MASKS[elementBits][bitPos] = 0;
                    continue;
                }
                int rBits = elementBits - (32 - bitPos);
                currentShifts[base + 2] = 32 - rBits;
                Packed32.MASKS[elementBits][bitPos] = ~(-1 << rBits);
            }
        }
        WRITE_MASKS = new int[33][99];
        for (elementBits = 1; elementBits <= 32; ++elementBits) {
            int elementPosMask = ~(-1 << elementBits);
            currentShifts = SHIFTS[elementBits];
            int[] currentMasks = WRITE_MASKS[elementBits];
            for (int bitPos = 0; bitPos < 32; ++bitPos) {
                int base = bitPos * 3;
                currentMasks[base] = ~(elementPosMask << currentShifts[base + 1] >>> currentShifts[base]);
                if (bitPos <= 32 - elementBits) {
                    currentMasks[base + 1] = -1;
                    currentMasks[base + 2] = 0;
                    continue;
                }
                currentMasks[base + 1] = ~(elementPosMask << currentShifts[base + 2]);
                currentMasks[base + 2] = currentShifts[base + 2] == 0 ? 0 : -1;
            }
        }
    }
}

