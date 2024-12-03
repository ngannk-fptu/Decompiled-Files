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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
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
public class MappedByteBufferPool
extends AbstractByteBufferPool
implements Dumpable {
    private static final Logger LOG = LoggerFactory.getLogger(MappedByteBufferPool.class);
    private final ConcurrentMap<Integer, AbstractByteBufferPool.Bucket> _directBuffers = new ConcurrentHashMap<Integer, AbstractByteBufferPool.Bucket>();
    private final ConcurrentMap<Integer, AbstractByteBufferPool.Bucket> _heapBuffers = new ConcurrentHashMap<Integer, AbstractByteBufferPool.Bucket>();
    private final Function<Integer, AbstractByteBufferPool.Bucket> _newBucket;
    private boolean _detailedDump = false;

    public MappedByteBufferPool() {
        this(-1);
    }

    public MappedByteBufferPool(int factor) {
        this(factor, -1);
    }

    public MappedByteBufferPool(int factor, int maxBucketSize) {
        this(factor, maxBucketSize, null);
    }

    private MappedByteBufferPool(int factor, int maxBucketSize, Function<Integer, AbstractByteBufferPool.Bucket> newBucket) {
        this(factor, maxBucketSize, newBucket, 0L, 0L, 0L, 0L);
    }

    public MappedByteBufferPool(int factor, int maxBucketSize, long maxHeapMemory, long maxDirectMemory) {
        this(factor, maxBucketSize, null, maxHeapMemory, maxDirectMemory, maxHeapMemory, maxDirectMemory);
    }

    public MappedByteBufferPool(int factor, int maxBucketSize, long maxHeapMemory, long maxDirectMemory, long retainedHeapMemory, long retainedDirectMemory) {
        this(factor, maxBucketSize, null, maxHeapMemory, maxDirectMemory, retainedHeapMemory, retainedDirectMemory);
    }

    private MappedByteBufferPool(int factor, int maxBucketSize, Function<Integer, AbstractByteBufferPool.Bucket> newBucket, long maxHeapMemory, long maxDirectMemory, long retainedHeapMemory, long retainedDirectMemory) {
        super(factor, 0, maxBucketSize, maxHeapMemory, maxDirectMemory, retainedHeapMemory, retainedDirectMemory);
        this._newBucket = newBucket;
    }

    @Override
    protected RetainableByteBufferPool newRetainableByteBufferPool(int factor, int maxCapacity, int maxBucketSize, long retainedHeapMemory, long retainedDirectMemory) {
        return new Retained(factor, maxCapacity, maxBucketSize, retainedHeapMemory, retainedDirectMemory);
    }

    private AbstractByteBufferPool.Bucket newBucket(int key, boolean direct) {
        return this._newBucket != null ? this._newBucket.apply(key) : new AbstractByteBufferPool.Bucket(this.capacityFor(key), this.getMaxBucketSize(), this.updateMemory(direct));
    }

    @Override
    public ByteBuffer acquire(int size, boolean direct) {
        int b = this.bucketFor(size);
        int capacity = this.capacityFor(b);
        ConcurrentMap<Integer, AbstractByteBufferPool.Bucket> buffers = this.bucketsFor(direct);
        AbstractByteBufferPool.Bucket bucket = (AbstractByteBufferPool.Bucket)buffers.get(b);
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
        int b;
        if (buffer == null) {
            return;
        }
        int capacity = buffer.capacity();
        if (capacity != this.capacityFor(b = this.bucketFor(capacity))) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("ByteBuffer {} does not belong to this pool, discarding it", (Object)BufferUtil.toDetailString((ByteBuffer)buffer));
            }
            return;
        }
        boolean direct = buffer.isDirect();
        ConcurrentMap<Integer, AbstractByteBufferPool.Bucket> buckets = this.bucketsFor(direct);
        AbstractByteBufferPool.Bucket bucket = buckets.computeIfAbsent(b, i -> this.newBucket((int)i, direct));
        bucket.release(buffer);
        this.releaseExcessMemory(direct, this::releaseMemory);
    }

    @Override
    public void clear() {
        super.clear();
        this._directBuffers.values().forEach(AbstractByteBufferPool.Bucket::clear);
        this._directBuffers.clear();
        this._heapBuffers.values().forEach(AbstractByteBufferPool.Bucket::clear);
        this._heapBuffers.clear();
    }

    protected void releaseMemory(boolean direct) {
        AbstractByteBufferPool.Bucket bucket;
        long oldest = Long.MAX_VALUE;
        int index = -1;
        ConcurrentMap<Integer, AbstractByteBufferPool.Bucket> buckets = this.bucketsFor(direct);
        for (Map.Entry entry : buckets.entrySet()) {
            AbstractByteBufferPool.Bucket bucket2 = (AbstractByteBufferPool.Bucket)entry.getValue();
            if (bucket2.isEmpty()) continue;
            long lastUpdateNanoTime = bucket2.getLastUpdate();
            if (oldest != Long.MAX_VALUE && !NanoTime.isBefore((long)lastUpdateNanoTime, (long)oldest)) continue;
            oldest = lastUpdateNanoTime;
            index = (Integer)entry.getKey();
        }
        if (index >= 0 && (bucket = (AbstractByteBufferPool.Bucket)buckets.remove(index)) != null) {
            bucket.clear();
        }
    }

    protected int bucketFor(int capacity) {
        return (int)Math.ceil((double)capacity / (double)this.getCapacityFactor());
    }

    protected int capacityFor(int bucket) {
        return bucket * this.getCapacityFactor();
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
        return this.bucketsFor(direct).values().stream().mapToLong(AbstractByteBufferPool.Bucket::size).sum();
    }

    ConcurrentMap<Integer, AbstractByteBufferPool.Bucket> bucketsFor(boolean direct) {
        return direct ? this._directBuffers : this._heapBuffers;
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
        if (this.isDetailedDump()) {
            dump.add(new DumpableCollection("Indirect Buckets", this._heapBuffers.values()));
            dump.add(new DumpableCollection("Direct Buckets", this._directBuffers.values()));
        } else {
            dump.add("Indirect Buckets size=" + this._heapBuffers.size());
            dump.add("Direct Buckets size=" + this._directBuffers.size());
        }
        Dumpable.dumpObjects((Appendable)out, (String)indent, (Object)this, (Object[])new Object[]{dump});
    }

    public String toString() {
        return String.format("%s@%x{maxQueueLength=%s, factor=%s}", this.getClass().getSimpleName(), this.hashCode(), this.getMaxBucketSize(), this.getCapacityFactor());
    }

    protected class Retained
    extends ArrayRetainableByteBufferPool {
        public Retained(int factor, int maxCapacity, int maxBucketSize, long retainedHeapMemory, long retainedDirectMemory) {
            super(0, factor, maxCapacity, maxBucketSize, retainedHeapMemory, retainedDirectMemory);
        }

        @Override
        protected ByteBuffer allocate(int capacity) {
            return MappedByteBufferPool.this.acquire(capacity, false);
        }

        @Override
        protected ByteBuffer allocateDirect(int capacity) {
            return MappedByteBufferPool.this.acquire(capacity, true);
        }

        @Override
        protected void removed(RetainableByteBuffer retainedBuffer) {
            MappedByteBufferPool.this.release(retainedBuffer.getBuffer());
        }
    }

    public static class Tagged
    extends MappedByteBufferPool {
        private final AtomicInteger tag = new AtomicInteger();

        @Override
        public ByteBuffer newByteBuffer(int capacity, boolean direct) {
            ByteBuffer buffer = super.newByteBuffer(capacity + 4, direct);
            buffer.limit(buffer.capacity());
            buffer.putInt(this.tag.incrementAndGet());
            ByteBuffer slice = buffer.slice();
            BufferUtil.clear((ByteBuffer)slice);
            return slice;
        }
    }
}

