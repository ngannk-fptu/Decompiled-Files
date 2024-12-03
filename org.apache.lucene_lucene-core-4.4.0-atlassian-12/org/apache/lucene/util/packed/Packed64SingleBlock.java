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

abstract class Packed64SingleBlock
extends PackedInts.MutableImpl {
    public static final int MAX_SUPPORTED_BITS_PER_VALUE = 32;
    private static final int[] SUPPORTED_BITS_PER_VALUE = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 16, 21, 32};
    final long[] blocks;

    public static boolean isSupported(int bitsPerValue) {
        return Arrays.binarySearch(SUPPORTED_BITS_PER_VALUE, bitsPerValue) >= 0;
    }

    private static int requiredCapacity(int valueCount, int valuesPerBlock) {
        return valueCount / valuesPerBlock + (valueCount % valuesPerBlock == 0 ? 0 : 1);
    }

    Packed64SingleBlock(int valueCount, int bitsPerValue) {
        super(valueCount, bitsPerValue);
        assert (Packed64SingleBlock.isSupported(bitsPerValue));
        int valuesPerBlock = 64 / bitsPerValue;
        this.blocks = new long[Packed64SingleBlock.requiredCapacity(valueCount, valuesPerBlock)];
    }

    @Override
    public void clear() {
        Arrays.fill(this.blocks, 0L);
    }

    @Override
    public long ramBytesUsed() {
        return RamUsageEstimator.alignObjectSize(RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 8 + RamUsageEstimator.NUM_BYTES_OBJECT_REF) + RamUsageEstimator.sizeOf(this.blocks);
    }

    @Override
    public int get(int index, long[] arr, int off, int len) {
        assert (len > 0) : "len must be > 0 (got " + len + ")";
        assert (index >= 0 && index < this.valueCount);
        len = Math.min(len, this.valueCount - index);
        assert (off + len <= arr.length);
        int originalIndex = index;
        int valuesPerBlock = 64 / this.bitsPerValue;
        int offsetInBlock = index % valuesPerBlock;
        if (offsetInBlock != 0) {
            for (int i = offsetInBlock; i < valuesPerBlock && len > 0; --len, ++i) {
                arr[off++] = this.get(index++);
            }
            if (len == 0) {
                return index - originalIndex;
            }
        }
        assert (index % valuesPerBlock == 0);
        BulkOperation decoder = BulkOperation.of(PackedInts.Format.PACKED_SINGLE_BLOCK, this.bitsPerValue);
        assert (decoder.longBlockCount() == 1);
        assert (decoder.longValueCount() == valuesPerBlock);
        int blockIndex = index / valuesPerBlock;
        int nblocks = (index + len) / valuesPerBlock - blockIndex;
        decoder.decode(this.blocks, blockIndex, arr, off, nblocks);
        int diff = nblocks * valuesPerBlock;
        len -= diff;
        if ((index += diff) > originalIndex) {
            return index - originalIndex;
        }
        assert (index == originalIndex);
        return super.get(index, arr, off, len);
    }

    @Override
    public int set(int index, long[] arr, int off, int len) {
        assert (len > 0) : "len must be > 0 (got " + len + ")";
        assert (index >= 0 && index < this.valueCount);
        len = Math.min(len, this.valueCount - index);
        assert (off + len <= arr.length);
        int originalIndex = index;
        int valuesPerBlock = 64 / this.bitsPerValue;
        int offsetInBlock = index % valuesPerBlock;
        if (offsetInBlock != 0) {
            for (int i = offsetInBlock; i < valuesPerBlock && len > 0; --len, ++i) {
                this.set(index++, arr[off++]);
            }
            if (len == 0) {
                return index - originalIndex;
            }
        }
        assert (index % valuesPerBlock == 0);
        BulkOperation op = BulkOperation.of(PackedInts.Format.PACKED_SINGLE_BLOCK, this.bitsPerValue);
        assert (op.longBlockCount() == 1);
        assert (op.longValueCount() == valuesPerBlock);
        int blockIndex = index / valuesPerBlock;
        int nblocks = (index + len) / valuesPerBlock - blockIndex;
        op.encode(arr, off, this.blocks, blockIndex, nblocks);
        int diff = nblocks * valuesPerBlock;
        len -= diff;
        if ((index += diff) > originalIndex) {
            return index - originalIndex;
        }
        assert (index == originalIndex);
        return super.set(index, arr, off, len);
    }

    @Override
    public void fill(int fromIndex, int toIndex, long val) {
        int i;
        assert (fromIndex >= 0);
        assert (fromIndex <= toIndex);
        assert (PackedInts.bitsRequired(val) <= this.bitsPerValue);
        int valuesPerBlock = 64 / this.bitsPerValue;
        if (toIndex - fromIndex <= valuesPerBlock << 1) {
            super.fill(fromIndex, toIndex, val);
            return;
        }
        int fromOffsetInBlock = fromIndex % valuesPerBlock;
        if (fromOffsetInBlock != 0) {
            for (int i2 = fromOffsetInBlock; i2 < valuesPerBlock; ++i2) {
                this.set(fromIndex++, val);
            }
            assert (fromIndex % valuesPerBlock == 0);
        }
        int fromBlock = fromIndex / valuesPerBlock;
        int toBlock = toIndex / valuesPerBlock;
        assert (fromBlock * valuesPerBlock == fromIndex);
        long blockValue = 0L;
        for (i = 0; i < valuesPerBlock; ++i) {
            blockValue |= val << i * this.bitsPerValue;
        }
        Arrays.fill(this.blocks, fromBlock, toBlock, blockValue);
        for (i = valuesPerBlock * toBlock; i < toIndex; ++i) {
            this.set(i, val);
        }
    }

    @Override
    protected PackedInts.Format getFormat() {
        return PackedInts.Format.PACKED_SINGLE_BLOCK;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(bitsPerValue=" + this.bitsPerValue + ", size=" + this.size() + ", elements.length=" + this.blocks.length + ")";
    }

    public static Packed64SingleBlock create(DataInput in, int valueCount, int bitsPerValue) throws IOException {
        Packed64SingleBlock reader = Packed64SingleBlock.create(valueCount, bitsPerValue);
        for (int i = 0; i < reader.blocks.length; ++i) {
            reader.blocks[i] = in.readLong();
        }
        return reader;
    }

    public static Packed64SingleBlock create(int valueCount, int bitsPerValue) {
        switch (bitsPerValue) {
            case 1: {
                return new Packed64SingleBlock1(valueCount);
            }
            case 2: {
                return new Packed64SingleBlock2(valueCount);
            }
            case 3: {
                return new Packed64SingleBlock3(valueCount);
            }
            case 4: {
                return new Packed64SingleBlock4(valueCount);
            }
            case 5: {
                return new Packed64SingleBlock5(valueCount);
            }
            case 6: {
                return new Packed64SingleBlock6(valueCount);
            }
            case 7: {
                return new Packed64SingleBlock7(valueCount);
            }
            case 8: {
                return new Packed64SingleBlock8(valueCount);
            }
            case 9: {
                return new Packed64SingleBlock9(valueCount);
            }
            case 10: {
                return new Packed64SingleBlock10(valueCount);
            }
            case 12: {
                return new Packed64SingleBlock12(valueCount);
            }
            case 16: {
                return new Packed64SingleBlock16(valueCount);
            }
            case 21: {
                return new Packed64SingleBlock21(valueCount);
            }
            case 32: {
                return new Packed64SingleBlock32(valueCount);
            }
        }
        throw new IllegalArgumentException("Unsupported number of bits per value: 32");
    }

    static class Packed64SingleBlock32
    extends Packed64SingleBlock {
        Packed64SingleBlock32(int valueCount) {
            super(valueCount, 32);
        }

        @Override
        public long get(int index) {
            int o = index >>> 1;
            int b = index & 1;
            int shift = b << 5;
            return this.blocks[o] >>> shift & 0xFFFFFFFFL;
        }

        @Override
        public void set(int index, long value) {
            int o = index >>> 1;
            int b = index & 1;
            int shift = b << 5;
            this.blocks[o] = this.blocks[o] & (0xFFFFFFFFL << shift ^ 0xFFFFFFFFFFFFFFFFL) | value << shift;
        }
    }

    static class Packed64SingleBlock21
    extends Packed64SingleBlock {
        Packed64SingleBlock21(int valueCount) {
            super(valueCount, 21);
        }

        @Override
        public long get(int index) {
            int o = index / 3;
            int b = index % 3;
            int shift = b * 21;
            return this.blocks[o] >>> shift & 0x1FFFFFL;
        }

        @Override
        public void set(int index, long value) {
            int o = index / 3;
            int b = index % 3;
            int shift = b * 21;
            this.blocks[o] = this.blocks[o] & (0x1FFFFFL << shift ^ 0xFFFFFFFFFFFFFFFFL) | value << shift;
        }
    }

    static class Packed64SingleBlock16
    extends Packed64SingleBlock {
        Packed64SingleBlock16(int valueCount) {
            super(valueCount, 16);
        }

        @Override
        public long get(int index) {
            int o = index >>> 2;
            int b = index & 3;
            int shift = b << 4;
            return this.blocks[o] >>> shift & 0xFFFFL;
        }

        @Override
        public void set(int index, long value) {
            int o = index >>> 2;
            int b = index & 3;
            int shift = b << 4;
            this.blocks[o] = this.blocks[o] & (65535L << shift ^ 0xFFFFFFFFFFFFFFFFL) | value << shift;
        }
    }

    static class Packed64SingleBlock12
    extends Packed64SingleBlock {
        Packed64SingleBlock12(int valueCount) {
            super(valueCount, 12);
        }

        @Override
        public long get(int index) {
            int o = index / 5;
            int b = index % 5;
            int shift = b * 12;
            return this.blocks[o] >>> shift & 0xFFFL;
        }

        @Override
        public void set(int index, long value) {
            int o = index / 5;
            int b = index % 5;
            int shift = b * 12;
            this.blocks[o] = this.blocks[o] & (4095L << shift ^ 0xFFFFFFFFFFFFFFFFL) | value << shift;
        }
    }

    static class Packed64SingleBlock10
    extends Packed64SingleBlock {
        Packed64SingleBlock10(int valueCount) {
            super(valueCount, 10);
        }

        @Override
        public long get(int index) {
            int o = index / 6;
            int b = index % 6;
            int shift = b * 10;
            return this.blocks[o] >>> shift & 0x3FFL;
        }

        @Override
        public void set(int index, long value) {
            int o = index / 6;
            int b = index % 6;
            int shift = b * 10;
            this.blocks[o] = this.blocks[o] & (1023L << shift ^ 0xFFFFFFFFFFFFFFFFL) | value << shift;
        }
    }

    static class Packed64SingleBlock9
    extends Packed64SingleBlock {
        Packed64SingleBlock9(int valueCount) {
            super(valueCount, 9);
        }

        @Override
        public long get(int index) {
            int o = index / 7;
            int b = index % 7;
            int shift = b * 9;
            return this.blocks[o] >>> shift & 0x1FFL;
        }

        @Override
        public void set(int index, long value) {
            int o = index / 7;
            int b = index % 7;
            int shift = b * 9;
            this.blocks[o] = this.blocks[o] & (511L << shift ^ 0xFFFFFFFFFFFFFFFFL) | value << shift;
        }
    }

    static class Packed64SingleBlock8
    extends Packed64SingleBlock {
        Packed64SingleBlock8(int valueCount) {
            super(valueCount, 8);
        }

        @Override
        public long get(int index) {
            int o = index >>> 3;
            int b = index & 7;
            int shift = b << 3;
            return this.blocks[o] >>> shift & 0xFFL;
        }

        @Override
        public void set(int index, long value) {
            int o = index >>> 3;
            int b = index & 7;
            int shift = b << 3;
            this.blocks[o] = this.blocks[o] & (255L << shift ^ 0xFFFFFFFFFFFFFFFFL) | value << shift;
        }
    }

    static class Packed64SingleBlock7
    extends Packed64SingleBlock {
        Packed64SingleBlock7(int valueCount) {
            super(valueCount, 7);
        }

        @Override
        public long get(int index) {
            int o = index / 9;
            int b = index % 9;
            int shift = b * 7;
            return this.blocks[o] >>> shift & 0x7FL;
        }

        @Override
        public void set(int index, long value) {
            int o = index / 9;
            int b = index % 9;
            int shift = b * 7;
            this.blocks[o] = this.blocks[o] & (127L << shift ^ 0xFFFFFFFFFFFFFFFFL) | value << shift;
        }
    }

    static class Packed64SingleBlock6
    extends Packed64SingleBlock {
        Packed64SingleBlock6(int valueCount) {
            super(valueCount, 6);
        }

        @Override
        public long get(int index) {
            int o = index / 10;
            int b = index % 10;
            int shift = b * 6;
            return this.blocks[o] >>> shift & 0x3FL;
        }

        @Override
        public void set(int index, long value) {
            int o = index / 10;
            int b = index % 10;
            int shift = b * 6;
            this.blocks[o] = this.blocks[o] & (63L << shift ^ 0xFFFFFFFFFFFFFFFFL) | value << shift;
        }
    }

    static class Packed64SingleBlock5
    extends Packed64SingleBlock {
        Packed64SingleBlock5(int valueCount) {
            super(valueCount, 5);
        }

        @Override
        public long get(int index) {
            int o = index / 12;
            int b = index % 12;
            int shift = b * 5;
            return this.blocks[o] >>> shift & 0x1FL;
        }

        @Override
        public void set(int index, long value) {
            int o = index / 12;
            int b = index % 12;
            int shift = b * 5;
            this.blocks[o] = this.blocks[o] & (31L << shift ^ 0xFFFFFFFFFFFFFFFFL) | value << shift;
        }
    }

    static class Packed64SingleBlock4
    extends Packed64SingleBlock {
        Packed64SingleBlock4(int valueCount) {
            super(valueCount, 4);
        }

        @Override
        public long get(int index) {
            int o = index >>> 4;
            int b = index & 0xF;
            int shift = b << 2;
            return this.blocks[o] >>> shift & 0xFL;
        }

        @Override
        public void set(int index, long value) {
            int o = index >>> 4;
            int b = index & 0xF;
            int shift = b << 2;
            this.blocks[o] = this.blocks[o] & (15L << shift ^ 0xFFFFFFFFFFFFFFFFL) | value << shift;
        }
    }

    static class Packed64SingleBlock3
    extends Packed64SingleBlock {
        Packed64SingleBlock3(int valueCount) {
            super(valueCount, 3);
        }

        @Override
        public long get(int index) {
            int o = index / 21;
            int b = index % 21;
            int shift = b * 3;
            return this.blocks[o] >>> shift & 7L;
        }

        @Override
        public void set(int index, long value) {
            int o = index / 21;
            int b = index % 21;
            int shift = b * 3;
            this.blocks[o] = this.blocks[o] & (7L << shift ^ 0xFFFFFFFFFFFFFFFFL) | value << shift;
        }
    }

    static class Packed64SingleBlock2
    extends Packed64SingleBlock {
        Packed64SingleBlock2(int valueCount) {
            super(valueCount, 2);
        }

        @Override
        public long get(int index) {
            int o = index >>> 5;
            int b = index & 0x1F;
            int shift = b << 1;
            return this.blocks[o] >>> shift & 3L;
        }

        @Override
        public void set(int index, long value) {
            int o = index >>> 5;
            int b = index & 0x1F;
            int shift = b << 1;
            this.blocks[o] = this.blocks[o] & (3L << shift ^ 0xFFFFFFFFFFFFFFFFL) | value << shift;
        }
    }

    static class Packed64SingleBlock1
    extends Packed64SingleBlock {
        Packed64SingleBlock1(int valueCount) {
            super(valueCount, 1);
        }

        @Override
        public long get(int index) {
            int o = index >>> 6;
            int b = index & 0x3F;
            int shift = b << 0;
            return this.blocks[o] >>> shift & 1L;
        }

        @Override
        public void set(int index, long value) {
            int o = index >>> 6;
            int b = index & 0x3F;
            int shift = b << 0;
            this.blocks[o] = this.blocks[o] & (1L << shift ^ 0xFFFFFFFFFFFFFFFFL) | value << shift;
        }
    }
}

