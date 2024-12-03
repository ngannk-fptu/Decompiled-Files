/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.io;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.catalina.tribes.io.BufferPool;
import org.apache.catalina.tribes.io.XByteBuffer;

class BufferPool15Impl
implements BufferPool.BufferPoolAPI {
    protected int maxSize;
    protected final AtomicInteger size = new AtomicInteger(0);
    protected final ConcurrentLinkedQueue<XByteBuffer> queue = new ConcurrentLinkedQueue();

    BufferPool15Impl() {
    }

    @Override
    public void setMaxSize(int bytes) {
        this.maxSize = bytes;
    }

    @Override
    public XByteBuffer getBuffer(int minSize, boolean discard) {
        XByteBuffer buffer = this.queue.poll();
        if (buffer != null) {
            this.size.addAndGet(-buffer.getCapacity());
        }
        if (buffer == null) {
            buffer = new XByteBuffer(minSize, discard);
        } else if (buffer.getCapacity() <= minSize) {
            buffer.expand(minSize);
        }
        buffer.setDiscard(discard);
        buffer.reset();
        return buffer;
    }

    @Override
    public void returnBuffer(XByteBuffer buffer) {
        if (this.size.get() + buffer.getCapacity() <= this.maxSize) {
            this.size.addAndGet(buffer.getCapacity());
            this.queue.offer(buffer);
        }
    }

    @Override
    public void clear() {
        this.queue.clear();
        this.size.set(0);
    }

    public int getMaxSize() {
        return this.maxSize;
    }
}

