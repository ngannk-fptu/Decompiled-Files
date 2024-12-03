/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.ByteBlockPool;
import com.atlassian.lucene36.util.RamUsageEstimator;
import java.util.concurrent.atomic.AtomicLong;

public final class RecyclingByteBlockAllocator
extends ByteBlockPool.Allocator {
    private byte[][] freeByteBlocks;
    private final int maxBufferedBlocks;
    private int freeBlocks = 0;
    private final AtomicLong bytesUsed;
    public static final int DEFAULT_BUFFERED_BLOCKS = 64;

    public RecyclingByteBlockAllocator(int blockSize, int maxBufferedBlocks, AtomicLong bytesUsed) {
        super(blockSize);
        this.freeByteBlocks = new byte[Math.min(10, maxBufferedBlocks)][];
        this.maxBufferedBlocks = maxBufferedBlocks;
        this.bytesUsed = bytesUsed;
    }

    public RecyclingByteBlockAllocator(int blockSize, int maxBufferedBlocks) {
        this(blockSize, maxBufferedBlocks, new AtomicLong());
    }

    public RecyclingByteBlockAllocator() {
        this(32768, 64, new AtomicLong());
    }

    public synchronized byte[] getByteBlock() {
        if (this.freeBlocks == 0) {
            this.bytesUsed.addAndGet(this.blockSize);
            return new byte[this.blockSize];
        }
        byte[] b = this.freeByteBlocks[--this.freeBlocks];
        this.freeByteBlocks[this.freeBlocks] = null;
        return b;
    }

    public synchronized void recycleByteBlocks(byte[][] blocks, int start, int end) {
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

    public synchronized int numBufferedBlocks() {
        return this.freeBlocks;
    }

    public long bytesUsed() {
        return this.bytesUsed.get();
    }

    public int maxBufferedBlocks() {
        return this.maxBufferedBlocks;
    }

    public synchronized int freeBlocks(int num) {
        int count;
        int stop;
        assert (num >= 0);
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

