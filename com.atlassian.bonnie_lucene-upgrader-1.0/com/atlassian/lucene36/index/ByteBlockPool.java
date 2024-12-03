/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.RamUsageEstimator;
import java.util.Arrays;
import java.util.List;

final class ByteBlockPool {
    public byte[][] buffers = new byte[10][];
    int bufferUpto = -1;
    public int byteUpto = 32768;
    public byte[] buffer;
    public int byteOffset = Short.MIN_VALUE;
    private final Allocator allocator;
    static final int[] nextLevelArray = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 9};
    static final int[] levelSizeArray = new int[]{5, 14, 20, 30, 40, 40, 80, 80, 120, 200};
    static final int FIRST_LEVEL_SIZE = levelSizeArray[0];

    public ByteBlockPool(Allocator allocator) {
        this.allocator = allocator;
    }

    public void reset() {
        if (this.bufferUpto != -1) {
            for (int i = 0; i < this.bufferUpto; ++i) {
                Arrays.fill(this.buffers[i], (byte)0);
            }
            Arrays.fill(this.buffers[this.bufferUpto], 0, this.byteUpto, (byte)0);
            if (this.bufferUpto > 0) {
                this.allocator.recycleByteBlocks(this.buffers, 1, 1 + this.bufferUpto);
            }
            this.bufferUpto = 0;
            this.byteUpto = 0;
            this.byteOffset = 0;
            this.buffer = this.buffers[0];
        }
    }

    public void nextBuffer() {
        if (1 + this.bufferUpto == this.buffers.length) {
            byte[][] newBuffers = new byte[ArrayUtil.oversize(this.buffers.length + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
            System.arraycopy(this.buffers, 0, newBuffers, 0, this.buffers.length);
            this.buffers = newBuffers;
        }
        byte[] byArray = this.allocator.getByteBlock();
        this.buffers[1 + this.bufferUpto] = byArray;
        this.buffer = byArray;
        ++this.bufferUpto;
        this.byteUpto = 0;
        this.byteOffset += 32768;
    }

    public int newSlice(int size) {
        if (this.byteUpto > 32768 - size) {
            this.nextBuffer();
        }
        int upto = this.byteUpto;
        this.byteUpto += size;
        this.buffer[this.byteUpto - 1] = 16;
        return upto;
    }

    public int allocSlice(byte[] slice, int upto) {
        int level = slice[upto] & 0xF;
        int newLevel = nextLevelArray[level];
        int newSize = levelSizeArray[newLevel];
        if (this.byteUpto > 32768 - newSize) {
            this.nextBuffer();
        }
        int newUpto = this.byteUpto;
        int offset = newUpto + this.byteOffset;
        this.byteUpto += newSize;
        this.buffer[newUpto] = slice[upto - 3];
        this.buffer[newUpto + 1] = slice[upto - 2];
        this.buffer[newUpto + 2] = slice[upto - 1];
        slice[upto - 3] = (byte)(offset >>> 24);
        slice[upto - 2] = (byte)(offset >>> 16);
        slice[upto - 1] = (byte)(offset >>> 8);
        slice[upto] = (byte)offset;
        this.buffer[this.byteUpto - 1] = (byte)(0x10 | newLevel);
        return newUpto + 3;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static abstract class Allocator {
        Allocator() {
        }

        abstract void recycleByteBlocks(byte[][] var1, int var2, int var3);

        abstract void recycleByteBlocks(List<byte[]> var1);

        abstract byte[] getByteBlock();
    }
}

