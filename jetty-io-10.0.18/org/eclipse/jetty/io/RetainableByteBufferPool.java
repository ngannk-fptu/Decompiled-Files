/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.io;

import java.nio.ByteBuffer;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.RetainableByteBuffer;

public interface RetainableByteBufferPool {
    public RetainableByteBuffer acquire(int var1, boolean var2);

    public void clear();

    public static RetainableByteBufferPool from(ByteBufferPool byteBufferPool) {
        return new NotRetainedByteBufferPool(byteBufferPool);
    }

    public static class NotRetainedByteBufferPool
    implements RetainableByteBufferPool {
        private final ByteBufferPool _byteBufferPool;

        public NotRetainedByteBufferPool(ByteBufferPool byteBufferPool) {
            this._byteBufferPool = byteBufferPool;
        }

        @Override
        public RetainableByteBuffer acquire(int size, boolean direct) {
            ByteBuffer byteBuffer = this._byteBufferPool.acquire(size, direct);
            RetainableByteBuffer retainableByteBuffer = new RetainableByteBuffer(byteBuffer, this::release);
            retainableByteBuffer.acquire();
            return retainableByteBuffer;
        }

        private void release(RetainableByteBuffer retainedBuffer) {
            this._byteBufferPool.release(retainedBuffer.getBuffer());
        }

        @Override
        public void clear() {
        }

        public String toString() {
            return String.format("NonRetainableByteBufferPool@%x{%s}", this.hashCode(), this._byteBufferPool.toString());
        }
    }
}

