/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Arrays;
import java.util.List;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.RamUsageEstimator;

public final class ByteBlockPool {
    public static final int BYTE_BLOCK_SHIFT = 15;
    public static final int BYTE_BLOCK_SIZE = 32768;
    public static final int BYTE_BLOCK_MASK = Short.MAX_VALUE;
    public byte[][] buffers = new byte[10][];
    private int bufferUpto = -1;
    public int byteUpto = 32768;
    public byte[] buffer;
    public int byteOffset = Short.MIN_VALUE;
    private final Allocator allocator;
    public static final int[] NEXT_LEVEL_ARRAY = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 9};
    public static final int[] LEVEL_SIZE_ARRAY = new int[]{5, 14, 20, 30, 40, 40, 80, 80, 120, 200};
    public static final int FIRST_LEVEL_SIZE = LEVEL_SIZE_ARRAY[0];

    public ByteBlockPool(Allocator allocator) {
        this.allocator = allocator;
    }

    public void reset() {
        this.reset(true, true);
    }

    public void reset(boolean zeroFillBuffers, boolean reuseFirst) {
        if (this.bufferUpto != -1) {
            if (zeroFillBuffers) {
                for (int i = 0; i < this.bufferUpto; ++i) {
                    Arrays.fill(this.buffers[i], (byte)0);
                }
                Arrays.fill(this.buffers[this.bufferUpto], 0, this.byteUpto, (byte)0);
            }
            if (this.bufferUpto > 0 || !reuseFirst) {
                int offset = reuseFirst ? 1 : 0;
                this.allocator.recycleByteBlocks(this.buffers, offset, 1 + this.bufferUpto);
                Arrays.fill((Object[])this.buffers, offset, 1 + this.bufferUpto, null);
            }
            if (reuseFirst) {
                this.bufferUpto = 0;
                this.byteUpto = 0;
                this.byteOffset = 0;
                this.buffer = this.buffers[0];
            } else {
                this.bufferUpto = -1;
                this.byteUpto = 32768;
                this.byteOffset = Short.MIN_VALUE;
                this.buffer = null;
            }
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
        int newLevel = NEXT_LEVEL_ARRAY[level];
        int newSize = LEVEL_SIZE_ARRAY[newLevel];
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

    public void setBytesRef(BytesRef term, int textStart) {
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
    }

    public void append(BytesRef bytes) {
        int length = bytes.length;
        if (length == 0) {
            return;
        }
        int offset = bytes.offset;
        int overflow = length + this.byteUpto - 32768;
        while (true) {
            if (overflow <= 0) {
                System.arraycopy(bytes.bytes, offset, this.buffer, this.byteUpto, length);
                this.byteUpto += length;
                break;
            }
            int bytesToCopy = length - overflow;
            if (bytesToCopy > 0) {
                System.arraycopy(bytes.bytes, offset, this.buffer, this.byteUpto, bytesToCopy);
                offset += bytesToCopy;
                length -= bytesToCopy;
            }
            this.nextBuffer();
            overflow -= 32768;
        }
    }

    public void readBytes(long offset, byte[] bytes, int off, int length) {
        if (length == 0) {
            return;
        }
        int bytesOffset = off;
        int bytesLength = length;
        int bufferIndex = (int)(offset >> 15);
        byte[] buffer = this.buffers[bufferIndex];
        int pos = (int)(offset & 0x7FFFL);
        int overflow = pos + length - 32768;
        while (true) {
            if (overflow <= 0) break;
            int bytesToCopy = length - overflow;
            System.arraycopy(buffer, pos, bytes, bytesOffset, bytesToCopy);
            pos = 0;
            bytesLength -= bytesToCopy;
            bytesOffset += bytesToCopy;
            buffer = this.buffers[++bufferIndex];
            overflow -= 32768;
        }
        System.arraycopy(buffer, pos, bytes, bytesOffset, bytesLength);
    }

    public static class DirectTrackingAllocator
    extends Allocator {
        private final Counter bytesUsed;

        public DirectTrackingAllocator(Counter bytesUsed) {
            this(32768, bytesUsed);
        }

        public DirectTrackingAllocator(int blockSize, Counter bytesUsed) {
            super(blockSize);
            this.bytesUsed = bytesUsed;
        }

        @Override
        public byte[] getByteBlock() {
            this.bytesUsed.addAndGet(this.blockSize);
            return new byte[this.blockSize];
        }

        @Override
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

        @Override
        public void recycleByteBlocks(byte[][] blocks, int start, int end) {
        }
    }

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

