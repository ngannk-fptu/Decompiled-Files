/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy.pool;

import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import org.xerial.snappy.pool.BufferPool;

public final class CachingBufferPool
implements BufferPool {
    private static final IntFunction<byte[]> ARRAY_FUNCTION = new IntFunction<byte[]>(){

        @Override
        public byte[] create(int n) {
            return new byte[n];
        }
    };
    private static final IntFunction<ByteBuffer> DBB_FUNCTION = new IntFunction<ByteBuffer>(){

        @Override
        public ByteBuffer create(int n) {
            return ByteBuffer.allocateDirect(n);
        }
    };
    private static final CachingBufferPool INSTANCE = new CachingBufferPool();
    private final ConcurrentMap<Integer, ConcurrentLinkedDeque<SoftReference<byte[]>>> bytes = new ConcurrentHashMap<Integer, ConcurrentLinkedDeque<SoftReference<byte[]>>>();
    private final ConcurrentMap<Integer, ConcurrentLinkedDeque<SoftReference<ByteBuffer>>> buffers = new ConcurrentHashMap<Integer, ConcurrentLinkedDeque<SoftReference<ByteBuffer>>>();

    private CachingBufferPool() {
    }

    public static BufferPool getInstance() {
        return INSTANCE;
    }

    @Override
    public byte[] allocateArray(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("size is invalid: " + n);
        }
        return CachingBufferPool.getOrCreate(n, this.bytes, ARRAY_FUNCTION);
    }

    @Override
    public void releaseArray(byte[] byArray) {
        if (byArray == null) {
            throw new IllegalArgumentException("buffer is null");
        }
        CachingBufferPool.returnValue(byArray, byArray.length, this.bytes);
    }

    @Override
    public ByteBuffer allocateDirect(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("size is invalid: " + n);
        }
        return CachingBufferPool.getOrCreate(n, this.buffers, DBB_FUNCTION);
    }

    @Override
    public void releaseDirect(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            throw new IllegalArgumentException("buffer is null");
        }
        byteBuffer.clear();
        CachingBufferPool.returnValue(byteBuffer, byteBuffer.capacity(), this.buffers);
    }

    private static <E> E getOrCreate(int n, ConcurrentMap<Integer, ConcurrentLinkedDeque<SoftReference<E>>> concurrentMap, IntFunction<E> intFunction) {
        SoftReference<E> softReference;
        assert (n > 0);
        int n2 = CachingBufferPool.adjustSize(n);
        ConcurrentLinkedDeque<SoftReference<E>> concurrentLinkedDeque = CachingBufferPool.optimisticGetEntry(n2, concurrentMap);
        while ((softReference = concurrentLinkedDeque.pollFirst()) != null) {
            E e = softReference.get();
            if (e == null) continue;
            return e;
        }
        return intFunction.create(n2);
    }

    static int adjustSize(int n) {
        assert (n > 0);
        switch (Integer.numberOfLeadingZeros(n)) {
            case 1: 
            case 2: {
                return n <= 0x60000000 ? CachingBufferPool.roundToPowers(n, 27) : Integer.MAX_VALUE;
            }
            case 3: 
            case 4: {
                return CachingBufferPool.roundToPowers(n, 24);
            }
            case 5: 
            case 6: 
            case 7: {
                return CachingBufferPool.roundToPowers(n, 22);
            }
            case 8: 
            case 9: 
            case 10: {
                return CachingBufferPool.roundToPowers(n, 19);
            }
            case 11: 
            case 12: {
                return CachingBufferPool.roundToPowers(n, 17);
            }
            case 13: 
            case 14: 
            case 15: 
            case 16: {
                return CachingBufferPool.roundToPowers(n, 14);
            }
            case 17: 
            case 18: 
            case 19: {
                return CachingBufferPool.roundToPowers(n, 11);
            }
        }
        return 4096;
    }

    private static int roundToPowers(int n, int n2) {
        int n3 = Integer.MAX_VALUE >> n2 << n2;
        int n4 = n & n3;
        return n4 == n ? n : n4 + (1 << n2);
    }

    private static <E> ConcurrentLinkedDeque<SoftReference<E>> optimisticGetEntry(Integer n, ConcurrentMap<Integer, ConcurrentLinkedDeque<SoftReference<E>>> concurrentMap) {
        ConcurrentLinkedDeque concurrentLinkedDeque = (ConcurrentLinkedDeque)concurrentMap.get(n);
        if (concurrentLinkedDeque == null) {
            concurrentMap.putIfAbsent(n, new ConcurrentLinkedDeque());
            concurrentLinkedDeque = (ConcurrentLinkedDeque)concurrentMap.get(n);
        }
        return concurrentLinkedDeque;
    }

    private static <E> void returnValue(E e, Integer n, ConcurrentMap<Integer, ConcurrentLinkedDeque<SoftReference<E>>> concurrentMap) {
        ConcurrentLinkedDeque concurrentLinkedDeque = (ConcurrentLinkedDeque)concurrentMap.get(n);
        if (concurrentLinkedDeque != null) {
            SoftReference softReference;
            concurrentLinkedDeque.addFirst(new SoftReference<E>(e));
            boolean bl = true;
            while (bl && (softReference = (SoftReference)concurrentLinkedDeque.peekLast()) != null) {
                if (softReference.get() == null) {
                    concurrentLinkedDeque.removeLastOccurrence(softReference);
                    continue;
                }
                bl = false;
            }
        }
    }

    public String toString() {
        return "CachingBufferPool [bytes=" + this.bytes + ", buffers=" + this.buffers + "]";
    }

    private static interface IntFunction<E> {
        public E create(int var1);
    }
}

