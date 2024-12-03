/*
 * Decompiled with CFR 0.152.
 */
package io.netty.buffer;

import io.netty.buffer.PoolChunk;
import io.netty.buffer.PoolSubpageMetric;
import java.util.concurrent.locks.ReentrantLock;

final class PoolSubpage<T>
implements PoolSubpageMetric {
    final PoolChunk<T> chunk;
    final int elemSize;
    private final int pageShifts;
    private final int runOffset;
    private final int runSize;
    private final long[] bitmap;
    private final int bitmapLength;
    private final int maxNumElems;
    final int headIndex;
    PoolSubpage<T> prev;
    PoolSubpage<T> next;
    boolean doNotDestroy;
    private int nextAvail;
    private int numAvail;
    final ReentrantLock lock;

    PoolSubpage(int headIndex) {
        this.chunk = null;
        this.lock = new ReentrantLock();
        this.pageShifts = -1;
        this.runOffset = -1;
        this.elemSize = -1;
        this.runSize = -1;
        this.bitmap = null;
        this.bitmapLength = -1;
        this.maxNumElems = 0;
        this.headIndex = headIndex;
    }

    PoolSubpage(PoolSubpage<T> head, PoolChunk<T> chunk, int pageShifts, int runOffset, int runSize, int elemSize) {
        this.headIndex = head.headIndex;
        this.chunk = chunk;
        this.pageShifts = pageShifts;
        this.runOffset = runOffset;
        this.runSize = runSize;
        this.elemSize = elemSize;
        this.doNotDestroy = true;
        this.maxNumElems = this.numAvail = runSize / elemSize;
        int bitmapLength = this.maxNumElems >>> 6;
        if ((this.maxNumElems & 0x3F) != 0) {
            ++bitmapLength;
        }
        this.bitmapLength = bitmapLength;
        this.bitmap = new long[bitmapLength];
        this.nextAvail = 0;
        this.lock = null;
        this.addToPool(head);
    }

    long allocate() {
        if (this.numAvail == 0 || !this.doNotDestroy) {
            return -1L;
        }
        int bitmapIdx = this.getNextAvail();
        if (bitmapIdx < 0) {
            this.removeFromPool();
            throw new AssertionError((Object)("No next available bitmap index found (bitmapIdx = " + bitmapIdx + "), even though there are supposed to be (numAvail = " + this.numAvail + ") out of (maxNumElems = " + this.maxNumElems + ") available indexes."));
        }
        int q = bitmapIdx >>> 6;
        int r = bitmapIdx & 0x3F;
        assert ((this.bitmap[q] >>> r & 1L) == 0L);
        int n = q;
        this.bitmap[n] = this.bitmap[n] | 1L << r;
        if (--this.numAvail == 0) {
            this.removeFromPool();
        }
        return this.toHandle(bitmapIdx);
    }

    boolean free(PoolSubpage<T> head, int bitmapIdx) {
        int q = bitmapIdx >>> 6;
        int r = bitmapIdx & 0x3F;
        assert ((this.bitmap[q] >>> r & 1L) != 0L);
        int n = q;
        this.bitmap[n] = this.bitmap[n] ^ 1L << r;
        this.setNextAvail(bitmapIdx);
        if (this.numAvail++ == 0) {
            this.addToPool(head);
            if (this.maxNumElems > 1) {
                return true;
            }
        }
        if (this.numAvail != this.maxNumElems) {
            return true;
        }
        if (this.prev == this.next) {
            return true;
        }
        this.doNotDestroy = false;
        this.removeFromPool();
        return false;
    }

    private void addToPool(PoolSubpage<T> head) {
        assert (this.prev == null && this.next == null);
        this.prev = head;
        this.next = head.next;
        this.next.prev = this;
        head.next = this;
    }

    private void removeFromPool() {
        assert (this.prev != null && this.next != null);
        this.prev.next = this.next;
        this.next.prev = this.prev;
        this.next = null;
        this.prev = null;
    }

    private void setNextAvail(int bitmapIdx) {
        this.nextAvail = bitmapIdx;
    }

    private int getNextAvail() {
        int nextAvail = this.nextAvail;
        if (nextAvail >= 0) {
            this.nextAvail = -1;
            return nextAvail;
        }
        return this.findNextAvail();
    }

    private int findNextAvail() {
        for (int i = 0; i < this.bitmapLength; ++i) {
            long bits = this.bitmap[i];
            if ((bits ^ 0xFFFFFFFFFFFFFFFFL) == 0L) continue;
            return this.findNextAvail0(i, bits);
        }
        return -1;
    }

    private int findNextAvail0(int i, long bits) {
        int baseVal = i << 6;
        for (int j = 0; j < 64; ++j) {
            if ((bits & 1L) == 0L) {
                int val = baseVal | j;
                if (val >= this.maxNumElems) break;
                return val;
            }
            bits >>>= 1;
        }
        return -1;
    }

    private long toHandle(int bitmapIdx) {
        int pages = this.runSize >> this.pageShifts;
        return (long)this.runOffset << 49 | (long)pages << 34 | 0x200000000L | 0x100000000L | (long)bitmapIdx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        int numAvail;
        if (this.chunk == null) {
            numAvail = 0;
        } else {
            boolean doNotDestroy;
            PoolSubpage head = this.chunk.arena.smallSubpagePools[this.headIndex];
            head.lock();
            try {
                doNotDestroy = this.doNotDestroy;
                numAvail = this.numAvail;
            }
            finally {
                head.unlock();
            }
            if (!doNotDestroy) {
                return "(" + this.runOffset + ": not in use)";
            }
        }
        return "(" + this.runOffset + ": " + (this.maxNumElems - numAvail) + '/' + this.maxNumElems + ", offset: " + this.runOffset + ", length: " + this.runSize + ", elemSize: " + this.elemSize + ')';
    }

    @Override
    public int maxNumElements() {
        return this.maxNumElems;
    }

    @Override
    public int numAvailable() {
        if (this.chunk == null) {
            return 0;
        }
        PoolSubpage head = this.chunk.arena.smallSubpagePools[this.headIndex];
        head.lock();
        try {
            int n = this.numAvail;
            return n;
        }
        finally {
            head.unlock();
        }
    }

    @Override
    public int elementSize() {
        return this.elemSize;
    }

    @Override
    public int pageSize() {
        return 1 << this.pageShifts;
    }

    boolean isDoNotDestroy() {
        if (this.chunk == null) {
            return true;
        }
        PoolSubpage head = this.chunk.arena.smallSubpagePools[this.headIndex];
        head.lock();
        try {
            boolean bl = this.doNotDestroy;
            return bl;
        }
        finally {
            head.unlock();
        }
    }

    void destroy() {
        if (this.chunk != null) {
            this.chunk.destroy();
        }
    }

    void lock() {
        this.lock.lock();
    }

    void unlock() {
        this.lock.unlock();
    }
}

