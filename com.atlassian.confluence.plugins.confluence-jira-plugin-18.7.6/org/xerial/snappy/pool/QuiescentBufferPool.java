/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy.pool;

import java.nio.ByteBuffer;
import org.xerial.snappy.pool.BufferPool;
import org.xerial.snappy.pool.DirectByteBuffers;

public final class QuiescentBufferPool
implements BufferPool {
    private static final QuiescentBufferPool INSTANCE = new QuiescentBufferPool();

    private QuiescentBufferPool() {
    }

    public static BufferPool getInstance() {
        return INSTANCE;
    }

    @Override
    public byte[] allocateArray(int n) {
        return new byte[n];
    }

    @Override
    public void releaseArray(byte[] byArray) {
    }

    @Override
    public ByteBuffer allocateDirect(int n) {
        return ByteBuffer.allocateDirect(n);
    }

    @Override
    public void releaseDirect(ByteBuffer byteBuffer) {
        assert (byteBuffer != null && byteBuffer.isDirect());
        DirectByteBuffers.releaseDirectByteBuffer(byteBuffer);
    }
}

