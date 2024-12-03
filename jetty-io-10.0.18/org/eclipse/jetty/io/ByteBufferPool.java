/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 */
package org.eclipse.jetty.io;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.io.RetainableByteBufferPool;
import org.eclipse.jetty.util.BufferUtil;

public interface ByteBufferPool {
    public ByteBuffer acquire(int var1, boolean var2);

    public void release(ByteBuffer var1);

    default public void remove(ByteBuffer buffer) {
    }

    default public ByteBuffer newByteBuffer(int capacity, boolean direct) {
        return direct ? BufferUtil.allocateDirect((int)capacity) : BufferUtil.allocate((int)capacity);
    }

    public RetainableByteBufferPool asRetainableByteBufferPool();

    public static class Lease {
        private final ByteBufferPool byteBufferPool;
        private final List<ByteBuffer> buffers;
        private final List<Boolean> recycles;

        public Lease(ByteBufferPool byteBufferPool) {
            this.byteBufferPool = byteBufferPool;
            this.buffers = new ArrayList<ByteBuffer>();
            this.recycles = new ArrayList<Boolean>();
        }

        public ByteBuffer acquire(int capacity, boolean direct) {
            ByteBuffer buffer = this.byteBufferPool.acquire(capacity, direct);
            BufferUtil.clearToFill((ByteBuffer)buffer);
            return buffer;
        }

        public void append(ByteBuffer buffer, boolean recycle) {
            this.buffers.add(buffer);
            this.recycles.add(recycle);
        }

        public void insert(int index, ByteBuffer buffer, boolean recycle) {
            this.buffers.add(index, buffer);
            this.recycles.add(index, recycle);
        }

        public List<ByteBuffer> getByteBuffers() {
            return this.buffers;
        }

        public long getTotalLength() {
            long length = 0L;
            for (ByteBuffer buffer : this.buffers) {
                length += (long)buffer.remaining();
            }
            return length;
        }

        public int getSize() {
            return this.buffers.size();
        }

        public void recycle() {
            for (int i = 0; i < this.buffers.size(); ++i) {
                ByteBuffer buffer = this.buffers.get(i);
                if (!this.recycles.get(i).booleanValue()) continue;
                this.release(buffer);
            }
            this.buffers.clear();
            this.recycles.clear();
        }

        public void release(ByteBuffer buffer) {
            this.byteBufferPool.release(buffer);
        }
    }
}

