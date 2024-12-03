/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.NanoTime
 *  org.eclipse.jetty.util.Pool
 *  org.eclipse.jetty.util.Pool$Entry
 *  org.eclipse.jetty.util.Pool$StrategyType
 *  org.eclipse.jetty.util.annotation.ManagedAttribute
 *  org.eclipse.jetty.util.annotation.ManagedObject
 *  org.eclipse.jetty.util.annotation.ManagedOperation
 *  org.eclipse.jetty.util.component.Dumpable
 *  org.eclipse.jetty.util.component.DumpableCollection
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import org.eclipse.jetty.io.AbstractByteBufferPool;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.RetainableByteBufferPool;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.Pool;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public class ArrayRetainableByteBufferPool
implements RetainableByteBufferPool,
Dumpable {
    private static final Logger LOG = LoggerFactory.getLogger(ArrayRetainableByteBufferPool.class);
    private final RetainedBucket[] _direct;
    private final RetainedBucket[] _indirect;
    private final int _minCapacity;
    private final int _maxCapacity;
    private final long _maxHeapMemory;
    private final long _maxDirectMemory;
    private final AtomicLong _currentHeapMemory = new AtomicLong();
    private final AtomicLong _currentDirectMemory = new AtomicLong();
    private final IntUnaryOperator _bucketIndexFor;

    public ArrayRetainableByteBufferPool() {
        this(0, -1, -1, Integer.MAX_VALUE);
    }

    public ArrayRetainableByteBufferPool(int minCapacity, int factor, int maxCapacity, int maxBucketSize) {
        this(minCapacity, factor, maxCapacity, maxBucketSize, 0L, 0L);
    }

    public ArrayRetainableByteBufferPool(int minCapacity, int factor, int maxCapacity, int maxBucketSize, long maxHeapMemory, long maxDirectMemory) {
        this(minCapacity, factor, maxCapacity, maxBucketSize, null, null, maxHeapMemory, maxDirectMemory);
    }

    @Deprecated
    protected ArrayRetainableByteBufferPool(int minCapacity, int factor, int maxCapacity, int maxBucketSize, long maxHeapMemory, long maxDirectMemory, Function<Integer, Integer> bucketIndexFor, Function<Integer, Integer> bucketCapacity) {
        this(minCapacity, factor, maxCapacity, maxBucketSize, bucketIndexFor::apply, bucketCapacity::apply, maxHeapMemory, maxDirectMemory);
    }

    protected ArrayRetainableByteBufferPool(int minCapacity, int factor, int maxCapacity, int maxBucketSize, IntUnaryOperator bucketIndexFor, IntUnaryOperator bucketCapacity, long maxHeapMemory, long maxDirectMemory) {
        if (minCapacity <= 0) {
            minCapacity = 0;
        }
        int n = factor = factor <= 0 ? 4096 : factor;
        if (maxCapacity <= 0) {
            maxCapacity = 16 * factor;
        }
        if (maxCapacity % factor != 0 || factor >= maxCapacity) {
            throw new IllegalArgumentException(String.format("The capacity factor(%d) must be a divisor of maxCapacity(%d)", factor, maxCapacity));
        }
        int f = factor;
        if (bucketIndexFor == null) {
            bucketIndexFor = c -> (c - 1) / f;
        }
        if (bucketCapacity == null) {
            bucketCapacity = i -> (i + 1) * f;
        }
        int length = bucketIndexFor.applyAsInt(maxCapacity) + 1;
        RetainedBucket[] directArray = new RetainedBucket[length];
        RetainedBucket[] indirectArray = new RetainedBucket[length];
        for (int i2 = 0; i2 < directArray.length; ++i2) {
            int capacity = Math.min(bucketCapacity.applyAsInt(i2), maxCapacity);
            directArray[i2] = new RetainedBucket(capacity, maxBucketSize);
            indirectArray[i2] = new RetainedBucket(capacity, maxBucketSize);
        }
        this._minCapacity = minCapacity;
        this._maxCapacity = maxCapacity;
        this._direct = directArray;
        this._indirect = indirectArray;
        this._maxHeapMemory = AbstractByteBufferPool.retainedSize(maxHeapMemory);
        this._maxDirectMemory = AbstractByteBufferPool.retainedSize(maxDirectMemory);
        this._bucketIndexFor = bucketIndexFor;
    }

    @ManagedAttribute(value="The minimum pooled buffer capacity")
    public int getMinCapacity() {
        return this._minCapacity;
    }

    @ManagedAttribute(value="The maximum pooled buffer capacity")
    public int getMaxCapacity() {
        return this._maxCapacity;
    }

    @Override
    public RetainableByteBuffer acquire(int size, boolean direct) {
        RetainableByteBuffer buffer;
        RetainedBucket bucket = this.bucketFor(size, direct);
        if (bucket == null) {
            return this.newRetainableByteBuffer(size, direct, this::removed);
        }
        Pool.Entry entry = bucket.acquire();
        if (entry == null) {
            Pool.Entry reservedEntry = bucket.reserve();
            if (reservedEntry != null) {
                buffer = this.newRetainableByteBuffer(bucket._capacity, direct, retainedBuffer -> {
                    BufferUtil.reset((ByteBuffer)retainedBuffer.getBuffer());
                    reservedEntry.release();
                });
                reservedEntry.enable((Object)buffer, true);
                if (direct) {
                    this._currentDirectMemory.addAndGet(buffer.capacity());
                } else {
                    this._currentHeapMemory.addAndGet(buffer.capacity());
                }
                this.releaseExcessMemory(direct);
            } else {
                buffer = this.newRetainableByteBuffer(size, direct, this::removed);
            }
        } else {
            buffer = (RetainableByteBuffer)entry.getPooled();
            buffer.acquire();
        }
        return buffer;
    }

    protected ByteBuffer allocate(int capacity) {
        return ByteBuffer.allocate(capacity);
    }

    protected ByteBuffer allocateDirect(int capacity) {
        return ByteBuffer.allocateDirect(capacity);
    }

    protected void removed(RetainableByteBuffer retainedBuffer) {
    }

    private RetainableByteBuffer newRetainableByteBuffer(int capacity, boolean direct, Consumer<RetainableByteBuffer> releaser) {
        ByteBuffer buffer = direct ? this.allocateDirect(capacity) : this.allocate(capacity);
        BufferUtil.clear((ByteBuffer)buffer);
        RetainableByteBuffer retainableByteBuffer = new RetainableByteBuffer(buffer, releaser);
        retainableByteBuffer.acquire();
        return retainableByteBuffer;
    }

    protected Pool<RetainableByteBuffer> poolFor(int capacity, boolean direct) {
        return this.bucketFor(capacity, direct);
    }

    private RetainedBucket bucketFor(int capacity, boolean direct) {
        RetainedBucket[] buckets;
        if (capacity < this._minCapacity) {
            return null;
        }
        int idx = this._bucketIndexFor.applyAsInt(capacity);
        RetainedBucket[] retainedBucketArray = buckets = direct ? this._direct : this._indirect;
        if (idx >= buckets.length) {
            return null;
        }
        return buckets[idx];
    }

    @ManagedAttribute(value="The number of pooled direct ByteBuffers")
    public long getDirectByteBufferCount() {
        return this.getByteBufferCount(true);
    }

    @ManagedAttribute(value="The number of pooled heap ByteBuffers")
    public long getHeapByteBufferCount() {
        return this.getByteBufferCount(false);
    }

    private long getByteBufferCount(boolean direct) {
        RetainedBucket[] buckets = direct ? this._direct : this._indirect;
        return Arrays.stream(buckets).mapToLong(Pool::size).sum();
    }

    @ManagedAttribute(value="The number of pooled direct ByteBuffers that are available")
    public long getAvailableDirectByteBufferCount() {
        return this.getAvailableByteBufferCount(true);
    }

    @ManagedAttribute(value="The number of pooled heap ByteBuffers that are available")
    public long getAvailableHeapByteBufferCount() {
        return this.getAvailableByteBufferCount(false);
    }

    private long getAvailableByteBufferCount(boolean direct) {
        RetainedBucket[] buckets = direct ? this._direct : this._indirect;
        return Arrays.stream(buckets).mapToLong(bucket -> bucket.values().stream().filter(Pool.Entry::isIdle).count()).sum();
    }

    @ManagedAttribute(value="The bytes retained by direct ByteBuffers")
    public long getDirectMemory() {
        return this.getMemory(true);
    }

    @ManagedAttribute(value="The bytes retained by heap ByteBuffers")
    public long getHeapMemory() {
        return this.getMemory(false);
    }

    private long getMemory(boolean direct) {
        if (direct) {
            return this._currentDirectMemory.get();
        }
        return this._currentHeapMemory.get();
    }

    @ManagedAttribute(value="The available bytes retained by direct ByteBuffers")
    public long getAvailableDirectMemory() {
        return this.getAvailableMemory(true);
    }

    @ManagedAttribute(value="The available bytes retained by heap ByteBuffers")
    public long getAvailableHeapMemory() {
        return this.getAvailableMemory(false);
    }

    private long getAvailableMemory(boolean direct) {
        RetainedBucket[] buckets = direct ? this._direct : this._indirect;
        long total = 0L;
        for (RetainedBucket bucket : buckets) {
            int capacity = bucket._capacity;
            total += bucket.values().stream().filter(Pool.Entry::isIdle).count() * (long)capacity;
        }
        return total;
    }

    @Override
    @ManagedOperation(value="Clears this RetainableByteBufferPool", impact="ACTION")
    public void clear() {
        this.clearArray(this._direct, this._currentDirectMemory);
        this.clearArray(this._indirect, this._currentHeapMemory);
    }

    private void clearArray(RetainedBucket[] poolArray, AtomicLong memoryCounter) {
        for (RetainedBucket pool : poolArray) {
            for (Pool.Entry entry : pool.values()) {
                if (!entry.remove()) continue;
                memoryCounter.addAndGet(-((RetainableByteBuffer)entry.getPooled()).capacity());
                this.removed((RetainableByteBuffer)entry.getPooled());
            }
        }
    }

    private void releaseExcessMemory(boolean direct) {
        long excess;
        long maxMemory;
        long l = maxMemory = direct ? this._maxDirectMemory : this._maxHeapMemory;
        if (maxMemory > 0L && (excess = this.getMemory(direct) - maxMemory) > 0L) {
            this.evict(direct, excess);
        }
    }

    private void evict(boolean direct, long excess) {
        RetainedBucket[] buckets;
        if (LOG.isDebugEnabled()) {
            LOG.debug("evicting {} bytes from {} pools", (Object)excess, (Object)(direct ? "direct" : "heap"));
        }
        long now = NanoTime.now();
        long totalClearedCapacity = 0L;
        RetainedBucket[] retainedBucketArray = buckets = direct ? this._direct : this._indirect;
        while (totalClearedCapacity < excess) {
            for (RetainedBucket bucket : buckets) {
                Pool.Entry oldestEntry = this.findOldestEntry(now, bucket);
                if (oldestEntry == null || !oldestEntry.remove()) continue;
                RetainableByteBuffer buffer = (RetainableByteBuffer)oldestEntry.getPooled();
                int clearedCapacity = buffer.capacity();
                if (direct) {
                    this._currentDirectMemory.addAndGet(-clearedCapacity);
                } else {
                    this._currentHeapMemory.addAndGet(-clearedCapacity);
                }
                totalClearedCapacity += (long)clearedCapacity;
                this.removed(buffer);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("eviction done, cleared {} bytes from {} pools", (Object)totalClearedCapacity, (Object)(direct ? "direct" : "heap"));
        }
    }

    public String toString() {
        return String.format("%s{min=%d,max=%d,buckets=%d,heap=%d/%d,direct=%d/%d}", super.toString(), this._minCapacity, this._maxCapacity, this._direct.length, this._currentHeapMemory.get(), this._maxHeapMemory, this._currentDirectMemory.get(), this._maxDirectMemory);
    }

    public void dump(Appendable out, String indent) throws IOException {
        Dumpable.dumpObjects((Appendable)out, (String)indent, (Object)this, (Object[])new Object[]{DumpableCollection.fromArray((String)"direct", (Object[])this._direct), DumpableCollection.fromArray((String)"indirect", (Object[])this._indirect)});
    }

    private Pool.Entry findOldestEntry(long now, Pool<RetainableByteBuffer> bucket) {
        Pool.Entry oldestEntry = null;
        RetainableByteBuffer oldestBuffer = null;
        long oldestAge = 0L;
        for (Pool.Entry entry : bucket.values()) {
            RetainableByteBuffer buffer = (RetainableByteBuffer)entry.getPooled();
            if (buffer == null) continue;
            long age = NanoTime.elapsed((long)buffer.getLastUpdate(), (long)now);
            if (oldestBuffer != null && age <= oldestAge) continue;
            oldestEntry = entry;
            oldestBuffer = buffer;
            oldestAge = age;
        }
        return oldestEntry;
    }

    private static class RetainedBucket
    extends Pool<RetainableByteBuffer> {
        private final int _capacity;

        RetainedBucket(int capacity, int size) {
            super(Pool.StrategyType.THREAD_ID, size, true);
            this._capacity = capacity;
        }

        public String toString() {
            int entries = 0;
            int inUse = 0;
            for (Pool.Entry entry : this.values()) {
                ++entries;
                if (!entry.isInUse()) continue;
                ++inUse;
            }
            return String.format("%s{capacity=%d,inuse=%d(%d%%)}", super.toString(), this._capacity, inUse, entries > 0 ? inUse * 100 / entries : 0);
        }
    }
}

