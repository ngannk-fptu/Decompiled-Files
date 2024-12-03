/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy.buffer;

import java.lang.ref.SoftReference;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import org.xerial.snappy.buffer.BufferAllocator;
import org.xerial.snappy.buffer.BufferAllocatorFactory;

public class CachedBufferAllocator
implements BufferAllocator {
    private static BufferAllocatorFactory factory = new BufferAllocatorFactory(){

        @Override
        public BufferAllocator getBufferAllocator(int n) {
            return CachedBufferAllocator.getAllocator(n);
        }
    };
    private static final Map<Integer, SoftReference<CachedBufferAllocator>> queueTable = new HashMap<Integer, SoftReference<CachedBufferAllocator>>();
    private final int bufferSize;
    private final Deque<byte[]> bufferQueue;

    public static void setBufferAllocatorFactory(BufferAllocatorFactory bufferAllocatorFactory) {
        assert (bufferAllocatorFactory != null);
        factory = bufferAllocatorFactory;
    }

    public static BufferAllocatorFactory getBufferAllocatorFactory() {
        return factory;
    }

    public CachedBufferAllocator(int n) {
        this.bufferSize = n;
        this.bufferQueue = new ArrayDeque<byte[]>();
    }

    public static synchronized CachedBufferAllocator getAllocator(int n) {
        CachedBufferAllocator cachedBufferAllocator = null;
        if (queueTable.containsKey(n)) {
            cachedBufferAllocator = queueTable.get(n).get();
        }
        if (cachedBufferAllocator == null) {
            cachedBufferAllocator = new CachedBufferAllocator(n);
            queueTable.put(n, new SoftReference<CachedBufferAllocator>(cachedBufferAllocator));
        }
        return cachedBufferAllocator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] allocate(int n) {
        CachedBufferAllocator cachedBufferAllocator = this;
        synchronized (cachedBufferAllocator) {
            if (this.bufferQueue.isEmpty()) {
                return new byte[n];
            }
            return this.bufferQueue.pollFirst();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void release(byte[] byArray) {
        CachedBufferAllocator cachedBufferAllocator = this;
        synchronized (cachedBufferAllocator) {
            this.bufferQueue.addLast(byArray);
        }
    }
}

