/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.store.DataOutput;
import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.BytesRef;
import com.atlassian.lucene36.util.RamUsageEstimator;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class ByteBlockPool {
    public static final int BYTE_BLOCK_SHIFT = 15;
    public static final int BYTE_BLOCK_SIZE = 32768;
    public static final int BYTE_BLOCK_MASK = Short.MAX_VALUE;
    public byte[][] buffers = new byte[10][];
    int bufferUpto = -1;
    public int byteUpto = 32768;
    public byte[] buffer;
    public int byteOffset = Short.MIN_VALUE;
    private final Allocator allocator;
    public static final int[] nextLevelArray = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 9};
    public static final int[] levelSizeArray = new int[]{5, 14, 20, 30, 40, 40, 80, 80, 120, 200};
    public static final int FIRST_LEVEL_SIZE = levelSizeArray[0];

    public ByteBlockPool(Allocator allocator) {
        this.allocator = allocator;
    }

    public void dropBuffersAndReset() {
        if (this.bufferUpto != -1) {
            this.allocator.recycleByteBlocks(this.buffers, 0, 1 + this.bufferUpto);
            this.bufferUpto = -1;
            this.byteUpto = 32768;
            this.byteOffset = Short.MIN_VALUE;
            this.buffers = new byte[10][];
            this.buffer = null;
        }
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

    public final BytesRef setBytesRef(BytesRef term, int textStart) {
        term.bytes = this.buffers[textStart >> 15];
        byte[] bytes = term.bytes;
        int pos = textStart & Short.MAX_VALUE;
        if ((bytes[pos] & 0x80) == 0) {
            term.length = bytes[pos];
            term.offset = pos + 1;
        } else {
            term.length = (bytes[pos] & 0x7F) + ((bytes[pos + 1] & 0xFF) << 7);
            term.offset = pos + 2;
        }
        assert (term.length >= 0);
        return term;
    }

    public final void copy(BytesRef bytes) {
        int length = bytes.length;
        int offset = bytes.offset;
        int overflow = length + this.byteUpto - 32768;
        while (true) {
            if (overflow <= 0) {
                System.arraycopy(bytes.bytes, offset, this.buffer, this.byteUpto, length);
                this.byteUpto += length;
                break;
            }
            int bytesToCopy = length - overflow;
            System.arraycopy(bytes.bytes, offset, this.buffer, this.byteUpto, bytesToCopy);
            offset += bytesToCopy;
            length -= bytesToCopy;
            this.nextBuffer();
            overflow -= 32768;
        }
    }

    public final BytesRef copyFrom(BytesRef bytes) {
        int length = bytes.length;
        int offset = bytes.offset;
        bytes.offset = 0;
        bytes.grow(length);
        int bufferIndex = offset >> 15;
        byte[] buffer = this.buffers[bufferIndex];
        int pos = offset & Short.MAX_VALUE;
        int overflow = pos + length - 32768;
        while (true) {
            if (overflow <= 0) break;
            int bytesToCopy = length - overflow;
            System.arraycopy(buffer, pos, bytes.bytes, bytes.offset, bytesToCopy);
            pos = 0;
            bytes.length -= bytesToCopy;
            bytes.offset += bytesToCopy;
            buffer = this.buffers[++bufferIndex];
            overflow -= 32768;
        }
        System.arraycopy(buffer, pos, bytes.bytes, bytes.offset, bytes.length);
        bytes.length = length;
        bytes.offset = 0;
        return bytes;
    }

    public final void writePool(DataOutput out) throws IOException {
        int block = 0;
        for (int bytesOffset = this.byteOffset; bytesOffset > 0; bytesOffset -= 32768) {
            out.writeBytes(this.buffers[block++], 32768);
        }
        out.writeBytes(this.buffers[block], this.byteUpto);
    }

    public static class DirectTrackingAllocator
    extends Allocator {
        private final AtomicLong bytesUsed;

        public DirectTrackingAllocator(AtomicLong bytesUsed) {
            this(32768, bytesUsed);
        }

        public DirectTrackingAllocator(int blockSize, AtomicLong bytesUsed) {
            super(blockSize);
            this.bytesUsed = bytesUsed;
        }

        public byte[] getByteBlock() {
            this.bytesUsed.addAndGet(this.blockSize);
            return new byte[this.blockSize];
        }

        public void recycleByteBlocks(byte[][] blocks, int start, int end) {
            this.bytesUsed.addAndGet(-((end - start) * this.blockSize));
            for (int i = start; i < end; ++i) {
                blocks[i] = null;
            }
        }
    }

    public static final class DirectAllocator
    extends Allocator {
        public DirectAllocator() {
            this(32768);
        }

        public DirectAllocator(int blockSize) {
            super(blockSize);
        }

        public void recycleByteBlocks(byte[][] blocks, int start, int end) {
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class Allocator {
        protected final int blockSize;

        public Allocator(int blockSize) {
            this.blockSize = blockSize;
        }

        public abstract void recycleByteBlocks(byte[][] var1, int var2, int var3);

        public void recycleByteBlocks(List<byte[]> blocks) {
            byte[][] b = (byte[][])blocks.toArray((T[])new byte[blocks.size()][]);
            this.recycleByteBlocks(b, 0, b.length);
        }

        public byte[] getByteBlock() {
            return new byte[this.blockSize];
        }
    }
}

