/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.RamUsageEstimator;

public final class RecyclingByteBlockAllocator
extends ByteBlockPool.Allocator {
    private byte[][] freeByteBlocks;
    private final int maxBufferedBlocks;
    private int freeBlocks = 0;
    private final Counter bytesUsed;
    public static final int DEFAULT_BUFFERED_BLOCKS = 64;

    public RecyclingByteBlockAllocator(int blockSize, int maxBufferedBlocks, Counter bytesUsed) {
        super(blockSize);
        this.freeByteBlocks = new byte[maxBufferedBlocks][];
        this.maxBufferedBlocks = maxBufferedBlocks;
        this.bytesUsed = bytesUsed;
    }

    public RecyclingByteBlockAllocator(int blockSize, int maxBufferedBlocks) {
        this(blockSize, maxBufferedBlocks, Counter.newCounter(false));
    }

    public RecyclingByteBlockAllocator() {
        this(32768, 64, Counter.newCounter(false));
    }

    @Override
    public byte[] getByteBlock() {
        if (this.freeBlocks == 0) {
            this.bytesUsed.addAndGet(this.blockSize);
            return new byte[this.blockSize];
        }
        byte[] b = this.freeByteBlocks[--this.freeBlocks];
        this.freeByteBlocks[this.freeBlocks] = null;
        return b;
    }

    @Override
    public void recycleByteBlocks(byte[][] blocks, int start, int end) {
        int i;
        int numBlocks = Math.min(this.maxBufferedBlocks - this.freeBlocks, end - start);
        int size = this.freeBlocks + numBlocks;
        if (size >= this.freeByteBlocks.length) {
            byte[][] newBlocks = new byte[ArrayUtil.oversize(size, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
            System.arraycopy(this.freeByteBlocks, 0, newBlocks, 0, this.freeBlocks);
            this.freeByteBlocks = newBlocks;
        }
        int stop = start + numBlocks;
        for (i = start; i < stop; ++i) {
            this.freeByteBlocks[this.freeBlocks++] = blocks[i];
            blocks[i] = null;
        }
        for (i = stop; i < end; ++i) {
            blocks[i] = null;
        }
        this.bytesUsed.addAndGet(-(end - stop) * this.blockSize);
        assert (this.bytesUsed.get() >= 0L);
    }

    public int numBufferedBlocks() {
        return this.freeBlocks;
    }

    public long bytesUsed() {
        return this.bytesUsed.get();
    }

    public int maxBufferedBlocks() {
        return this.maxBufferedBlocks;
    }

    public int freeBlocks(int num) {
        int count;
        int stop;
        assert (num >= 0) : "free blocks must be >= 0 but was: " + num;
        if (num > this.freeBlocks) {
            stop = 0;
            count = this.freeBlocks;
        } else {
            stop = this.freeBlocks - num;
            count = num;
        }
        while (this.freeBlocks > stop) {
            this.freeByteBlocks[--this.freeBlocks] = null;
        }
        this.bytesUsed.addAndGet(-count * this.blockSize);
        assert (this.bytesUsed.get() >= 0L);
        return count;
    }
}

