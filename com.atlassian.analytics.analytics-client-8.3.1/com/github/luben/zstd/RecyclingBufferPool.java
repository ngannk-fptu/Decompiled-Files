/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.BufferPool;
import com.github.luben.zstd.ZstdInputStreamNoFinalizer;
import com.github.luben.zstd.ZstdOutputStreamNoFinalizer;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

public class RecyclingBufferPool
implements BufferPool {
    public static final BufferPool INSTANCE = new RecyclingBufferPool();
    private static final int buffSize = Math.max(Math.max((int)ZstdOutputStreamNoFinalizer.recommendedCOutSize(), (int)ZstdInputStreamNoFinalizer.recommendedDInSize()), (int)ZstdInputStreamNoFinalizer.recommendedDOutSize());
    private final Deque<SoftReference<ByteBuffer>> pool = new ArrayDeque<SoftReference<ByteBuffer>>();

    private RecyclingBufferPool() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ByteBuffer get(int n) {
        SoftReference<ByteBuffer> softReference;
        Object object;
        if (n > buffSize) {
            throw new RuntimeException("Unsupported buffer size: " + n + ". Supported buffer sizes: " + buffSize + " or smaller.");
        }
        do {
            softReference = null;
            if (!this.pool.isEmpty()) {
                object = this.pool;
                synchronized (object) {
                    softReference = this.pool.pollFirst();
                }
            }
            if (softReference != null) continue;
            return ByteBuffer.allocate(buffSize);
        } while ((object = (ByteBuffer)softReference.get()) == null);
        return object;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void release(ByteBuffer byteBuffer) {
        if (byteBuffer.capacity() >= buffSize) {
            byteBuffer.clear();
            Deque<SoftReference<ByteBuffer>> deque = this.pool;
            synchronized (deque) {
                this.pool.addLast(new SoftReference<ByteBuffer>(byteBuffer));
            }
        }
    }
}

