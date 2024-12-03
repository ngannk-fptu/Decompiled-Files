/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.NanoTime
 *  org.eclipse.jetty.util.annotation.ManagedAttribute
 *  org.eclipse.jetty.util.annotation.ManagedObject
 *  org.eclipse.jetty.util.component.Dumpable
 *  org.eclipse.jetty.util.component.DumpableCollection
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.jetty.io.AbstractByteBufferPool;
import org.eclipse.jetty.io.ArrayRetainableByteBufferPool;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.RetainableByteBufferPool;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public class ArrayByteBufferPool
extends AbstractByteBufferPool
implements Dumpable {
    private static final Logger LOG = LoggerFactory.getLogger(ArrayByteBufferPool.class);
    private final int _maxCapacity;
    private final int _minCapacity;
    private final AbstractByteBufferPool.Bucket[] _direct;
    private final AbstractByteBufferPool.Bucket[] _indirect;
    private boolean _detailedDump = false;

    public ArrayByteBufferPool() {
        this(-1, -1, -1);
    }

    public ArrayByteBufferPool(int minCapacity, int factor, int maxCapacity) {
        this(minCapacity, factor, maxCapacity, -1, 0L, 0L);
    }

    public ArrayByteBufferPool(int minCapacity, int factor, int maxCapacity, int maxQueueLength) {
        this(minCapacity, factor, maxCapacity, maxQueueLength, 0L, 0L);
    }

    public ArrayByteBufferPool(int minCapacity, int factor, int maxCapacity, int maxBucketSize, long maxHeapMemory, long maxDirectMemory) {
        this(minCapacity, factor, maxCapacity, maxBucketSize, maxHeapMemory, maxDirectMemory, maxHeapMemory, maxDirectMemory);
    }

    public ArrayByteBufferPool(int minCapacity, int factor, int maxCapacity, int maxBucketSize, long maxHeapMemory, long maxDirectMemory, long retainedHeapMemory, long retainedDirectMemory) {
        super(factor, maxCapacity, maxBucketSize, maxHeapMemory, maxDirectMemory, retainedHeapMemory, retainedDirectMemory);
        maxCapacity = this.getMaxCapacity();
        factor = this.getCapacityFactor();
        if (minCapacity <= 0) {
            minCapacity = 0;
        }
        if (maxCapacity % factor != 0 || factor >= maxCapacity) {
            throw new IllegalArgumentException("The capacity factor must be a divisor of maxCapacity");
        }
        this._maxCapacity = maxCapacity;
        this._minCapacity = minCapacity;
        int length = this.bucketFor(maxCapacity) + 1;
        this._direct = new AbstractByteBufferPool.Bucket[length];
        this._indirect = new AbstractByteBufferPool.Bucket[length];
        for (int i = 0; i < length; ++i) {
            this._direct[i] = this.newBucket(i, true);
            this._indirect[i] = this.newBucket(i, false);
        }
    }

    @Override
    protected RetainableByteBufferPool newRetainableByteBufferPool(int factor, int maxCapacity, int maxBucketSize, long retainedHeapMemory, long retainedDirectMemory) {
        return new Retained(factor, maxCapacity, maxBucketSize, retainedHeapMemory, retainedDirectMemory);
    }

    @Override
    public ByteBuffer acquire(int size, boolean direct) {
        int capacity = size < this._minCapacity ? size : this.capacityFor(this.bucketFor(size));
        AbstractByteBufferPool.Bucket bucket = this.bucketFor(size, direct);
        if (bucket == null) {
            return this.newByteBuffer(capacity, direct);
        }
        ByteBuffer buffer = bucket.acquire();
        if (buffer == null) {
            return this.newByteBuffer(capacity, direct);
        }
        return buffer;
    }

    @Override
    public void release(ByteBuffer buffer) {
        if (buffer == null) {
            return;
        }
        int capacity = buffer.capacity();
        if (capacity != this.capacityFor(this.bucketFor(capacity))) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("ByteBuffer {} does not belong to this pool, discarding it", (Object)BufferUtil.toDetailString((ByteBuffer)buffer));
            }
            return;
        }
        if (capacity > this._maxCapacity) {
            return;
        }
        boolean direct = buffer.isDirect();
        AbstractByteBufferPool.Bucket bucket = this.bucketFor(capacity, direct);
        if (bucket != null) {
            bucket.release(buffer);
            this.releaseExcessMemory(direct, this::releaseMemory);
        }
    }

    private AbstractByteBufferPool.Bucket newBucket(int key, boolean direct) {
        return new AbstractByteBufferPool.Bucket(this.capacityFor(key), this.getMaxBucketSize(), this.updateMemory(direct));
    }

    @Override
    public void clear() {
        super.clear();
        for (int i = 0; i < this._direct.length; ++i) {
            this._direct[i].clear();
            this._indirect[i].clear();
        }
    }

    protected void releaseMemory(boolean direct) {
        long oldest = Long.MAX_VALUE;
        int index = -1;
        AbstractByteBufferPool.Bucket[] buckets = this.bucketsFor(direct);
        for (int i = 0; i < buckets.length; ++i) {
            AbstractByteBufferPool.Bucket bucket = buckets[i];
            if (bucket.isEmpty()) continue;
            long lastUpdateNanoTime = bucket.getLastUpdate();
            if (oldest != Long.MAX_VALUE && !NanoTime.isBefore((long)lastUpdateNanoTime, (long)oldest)) continue;
            oldest = lastUpdateNanoTime;
            index = i;
        }
        if (index >= 0) {
            AbstractByteBufferPool.Bucket bucket = buckets[index];
            bucket.clear();
        }
    }

    protected int bucketFor(int capacity) {
        return (int)Math.ceil((double)capacity / (double)this.getCapacityFactor());
    }

    protected int capacityFor(int bucket) {
        return bucket * this.getCapacityFactor();
    }

    protected AbstractByteBufferPool.Bucket bucketFor(int capacity, boolean direct) {
        if (capacity < this._minCapacity) {
            return null;
        }
        int bucket = this.bucketFor(capacity);
        if (bucket >= this._direct.length) {
            return null;
        }
        AbstractByteBufferPool.Bucket[] buckets = this.bucketsFor(direct);
        return buckets[bucket];
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
        return Arrays.stream(this.bucketsFor(direct)).filter(Objects::nonNull).mapToLong(AbstractByteBufferPool.Bucket::size).sum();
    }

    AbstractByteBufferPool.Bucket[] bucketsFor(boolean direct) {
        return direct ? this._direct : this._indirect;
    }

    public boolean isDetailedDump() {
        return this._detailedDump;
    }

    public void setDetailedDump(boolean detailedDump) {
        this._detailedDump = detailedDump;
    }

    public void dump(Appendable out, String indent) throws IOException {
        ArrayList<Object> dump = new ArrayList<Object>();
        dump.add(String.format("HeapMemory: %d/%d", this.getHeapMemory(), this.getMaxHeapMemory()));
        dump.add(String.format("DirectMemory: %d/%d", this.getDirectMemory(), this.getMaxDirectMemory()));
        List indirect = Arrays.stream(this._indirect).filter(b -> !b.isEmpty()).collect(Collectors.toList());
        List direct = Arrays.stream(this._direct).filter(b -> !b.isEmpty()).collect(Collectors.toList());
        if (this.isDetailedDump()) {
            dump.add(new DumpableCollection("Indirect Buckets", indirect));
            dump.add(new DumpableCollection("Direct Buckets", direct));
        } else {
            dump.add("Indirect Buckets size=" + indirect.size());
            dump.add("Direct Buckets size=" + direct.size());
        }
        dump.add(this.asRetainableByteBufferPool());
        Dumpable.dumpObjects((Appendable)out, (String)indent, (Object)this, (Object[])new Object[]{dump});
    }

    public String toString() {
        return String.format("%s@%x{minBufferCapacity=%s, maxBufferCapacity=%s, maxQueueLength=%s, factor=%s}", this.getClass().getSimpleName(), this.hashCode(), this._minCapacity, this._maxCapacity, this.getMaxBucketSize(), this.getCapacityFactor());
    }

    protected class Retained
    extends ArrayRetainableByteBufferPool {
        public Retained(int factor, int maxCapacity, int maxBucketSize, long retainedHeapMemory, long retainedDirectMemory) {
            super(0, factor, maxCapacity, maxBucketSize, retainedHeapMemory, retainedDirectMemory);
        }

        @Override
        protected ByteBuffer allocate(int capacity) {
            return ArrayByteBufferPool.this.acquire(capacity, false);
        }

        @Override
        protected ByteBuffer allocateDirect(int capacity) {
            return ArrayByteBufferPool.this.acquire(capacity, true);
        }

        @Override
        protected void removed(RetainableByteBuffer retainedBuffer) {
            ArrayByteBufferPool.this.release(retainedBuffer.getBuffer());
        }
    }
}

