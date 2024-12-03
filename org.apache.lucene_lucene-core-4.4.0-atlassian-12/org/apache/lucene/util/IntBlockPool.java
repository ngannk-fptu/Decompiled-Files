/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Arrays;

public final class IntBlockPool {
    public static final int INT_BLOCK_SHIFT = 13;
    public static final int INT_BLOCK_SIZE = 8192;
    public static final int INT_BLOCK_MASK = 8191;
    public int[][] buffers = new int[10][];
    private int bufferUpto = -1;
    public int intUpto = 8192;
    public int[] buffer;
    public int intOffset = -8192;
    private final Allocator allocator;
    private static final int[] NEXT_LEVEL_ARRAY = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 9};
    private static final int[] LEVEL_SIZE_ARRAY = new int[]{2, 4, 8, 16, 32, 64, 128, 256, 512, 1024};
    private static final int FIRST_LEVEL_SIZE = LEVEL_SIZE_ARRAY[0];

    public IntBlockPool() {
        this(new DirectAllocator());
    }

    public IntBlockPool(Allocator allocator) {
        this.allocator = allocator;
    }

    public void reset() {
        this.reset(true, true);
    }

    public void reset(boolean zeroFillBuffers, boolean reuseFirst) {
        if (this.bufferUpto != -1) {
            if (zeroFillBuffers) {
                for (int i = 0; i < this.bufferUpto; ++i) {
                    Arrays.fill(this.buffers[i], 0);
                }
                Arrays.fill(this.buffers[this.bufferUpto], 0, this.intUpto, 0);
            }
            if (this.bufferUpto > 0 || !reuseFirst) {
                int offset = reuseFirst ? 1 : 0;
                this.allocator.recycleIntBlocks(this.buffers, offset, 1 + this.bufferUpto);
                Arrays.fill((Object[])this.buffers, offset, this.bufferUpto + 1, null);
            }
            if (reuseFirst) {
                this.bufferUpto = 0;
                this.intUpto = 0;
                this.intOffset = 0;
                this.buffer = this.buffers[0];
            } else {
                this.bufferUpto = -1;
                this.intUpto = 8192;
                this.intOffset = -8192;
                this.buffer = null;
            }
        }
    }

    public void nextBuffer() {
        if (1 + this.bufferUpto == this.buffers.length) {
            int[][] newBuffers = new int[(int)((double)this.buffers.length * 1.5)][];
            System.arraycopy(this.buffers, 0, newBuffers, 0, this.buffers.length);
            this.buffers = newBuffers;
        }
        int[] nArray = this.allocator.getIntBlock();
        this.buffers[1 + this.bufferUpto] = nArray;
        this.buffer = nArray;
        ++this.bufferUpto;
        this.intUpto = 0;
        this.intOffset += 8192;
    }

    private int newSlice(int size) {
        if (this.intUpto > 8192 - size) {
            this.nextBuffer();
            assert (IntBlockPool.assertSliceBuffer(this.buffer));
        }
        int upto = this.intUpto;
        this.intUpto += size;
        this.buffer[this.intUpto - 1] = 1;
        return upto;
    }

    private static final boolean assertSliceBuffer(int[] buffer) {
        int count = 0;
        for (int i = 0; i < buffer.length; ++i) {
            count += buffer[i];
        }
        return count == 0;
    }

    private int allocSlice(int[] slice, int sliceOffset) {
        int level = slice[sliceOffset];
        int newLevel = NEXT_LEVEL_ARRAY[level - 1];
        int newSize = LEVEL_SIZE_ARRAY[newLevel];
        if (this.intUpto > 8192 - newSize) {
            this.nextBuffer();
            assert (IntBlockPool.assertSliceBuffer(this.buffer));
        }
        int newUpto = this.intUpto;
        int offset = newUpto + this.intOffset;
        this.intUpto += newSize;
        slice[sliceOffset] = offset;
        this.buffer[this.intUpto - 1] = newLevel;
        return newUpto;
    }

    public static final class SliceReader {
        private final IntBlockPool pool;
        private int upto;
        private int bufferUpto;
        private int bufferOffset;
        private int[] buffer;
        private int limit;
        private int level;
        private int end;

        public SliceReader(IntBlockPool pool) {
            this.pool = pool;
        }

        public void reset(int startOffset, int endOffset) {
            this.bufferUpto = startOffset / 8192;
            this.bufferOffset = this.bufferUpto * 8192;
            this.end = endOffset;
            this.upto = startOffset;
            this.level = 1;
            this.buffer = this.pool.buffers[this.bufferUpto];
            this.upto = startOffset & 0x1FFF;
            int firstSize = LEVEL_SIZE_ARRAY[0];
            this.limit = startOffset + firstSize >= endOffset ? endOffset & 0x1FFF : this.upto + firstSize - 1;
        }

        public boolean endOfSlice() {
            assert (this.upto + this.bufferOffset <= this.end);
            return this.upto + this.bufferOffset == this.end;
        }

        public int readInt() {
            assert (!this.endOfSlice());
            assert (this.upto <= this.limit);
            if (this.upto == this.limit) {
                this.nextSlice();
            }
            return this.buffer[this.upto++];
        }

        private void nextSlice() {
            int nextIndex = this.buffer[this.limit];
            this.level = NEXT_LEVEL_ARRAY[this.level - 1];
            int newSize = LEVEL_SIZE_ARRAY[this.level];
            this.bufferUpto = nextIndex / 8192;
            this.bufferOffset = this.bufferUpto * 8192;
            this.buffer = this.pool.buffers[this.bufferUpto];
            this.upto = nextIndex & 0x1FFF;
            if (nextIndex + newSize >= this.end) {
                assert (this.end - nextIndex > 0);
                this.limit = this.end - this.bufferOffset;
            } else {
                this.limit = this.upto + newSize - 1;
            }
        }
    }

    public static class SliceWriter {
        private int offset;
        private final IntBlockPool pool;

        public SliceWriter(IntBlockPool pool) {
            this.pool = pool;
        }

        public void reset(int sliceOffset) {
            this.offset = sliceOffset;
        }

        public void writeInt(int value) {
            int[] ints = this.pool.buffers[this.offset >> 13];
            assert (ints != null);
            int relativeOffset = this.offset & 0x1FFF;
            if (ints[relativeOffset] != 0) {
                relativeOffset = this.pool.allocSlice(ints, relativeOffset);
                ints = this.pool.buffer;
                this.offset = relativeOffset + this.pool.intOffset;
            }
            ints[relativeOffset] = value;
            ++this.offset;
        }

        public int startNewSlice() {
            this.offset = this.pool.newSlice(FIRST_LEVEL_SIZE) + this.pool.intOffset;
            return this.offset;
        }

        public int getCurrentOffset() {
            return this.offset;
        }
    }

    public static final class DirectAllocator
    extends Allocator {
        public DirectAllocator() {
            super(8192);
        }

        @Override
        public void recycleIntBlocks(int[][] blocks, int start, int end) {
        }
    }

    public static abstract class Allocator {
        protected final int blockSize;

        public Allocator(int blockSize) {
            this.blockSize = blockSize;
        }

        public abstract void recycleIntBlocks(int[][] var1, int var2, int var3);

        public int[] getIntBlock() {
            return new int[this.blockSize];
        }
    }
}

