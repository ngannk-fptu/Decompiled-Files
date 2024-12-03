/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.support.classic;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.nio.CapacityChannel;
import org.apache.hc.core5.http.nio.support.classic.AbstractSharedBuffer;
import org.apache.hc.core5.http.nio.support.classic.ContentInputBuffer;

@Contract(threading=ThreadingBehavior.SAFE)
public final class SharedInputBuffer
extends AbstractSharedBuffer
implements ContentInputBuffer {
    private final int initialBufferSize;
    private final AtomicInteger capacityIncrement;
    private volatile CapacityChannel capacityChannel;

    public SharedInputBuffer(ReentrantLock lock, int initialBufferSize) {
        super(lock, initialBufferSize);
        this.initialBufferSize = initialBufferSize;
        this.capacityIncrement = new AtomicInteger(0);
    }

    public SharedInputBuffer(int bufferSize) {
        this(new ReentrantLock(), bufferSize);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int fill(ByteBuffer src) {
        this.lock.lock();
        try {
            this.setInputMode();
            this.ensureAdjustedCapacity(this.buffer().position() + src.remaining());
            this.buffer().put(src);
            int remaining = this.buffer().remaining();
            this.condition.signalAll();
            int n = remaining;
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    private void incrementCapacity() throws IOException {
        int increment;
        if (this.capacityChannel != null && (increment = this.capacityIncrement.getAndSet(0)) > 0) {
            this.capacityChannel.update(increment);
        }
    }

    public void updateCapacity(CapacityChannel capacityChannel) throws IOException {
        this.lock.lock();
        try {
            this.capacityChannel = capacityChannel;
            this.setInputMode();
            if (this.buffer().position() == 0) {
                capacityChannel.update(this.initialBufferSize);
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    private void awaitInput() throws InterruptedIOException {
        if (!this.buffer().hasRemaining()) {
            this.setInputMode();
            while (this.buffer().position() == 0 && !this.endStream && !this.aborted) {
                try {
                    this.condition.await();
                }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new InterruptedIOException(ex.getMessage());
                }
            }
            this.setOutputMode();
        }
    }

    @Override
    public int read() throws IOException {
        this.lock.lock();
        try {
            this.setOutputMode();
            this.awaitInput();
            if (this.aborted) {
                int n = -1;
                return n;
            }
            if (!this.buffer().hasRemaining() && this.endStream) {
                int n = -1;
                return n;
            }
            int b = this.buffer().get() & 0xFF;
            this.capacityIncrement.incrementAndGet();
            if (!this.buffer().hasRemaining()) {
                this.incrementCapacity();
            }
            int n = b;
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        this.lock.lock();
        try {
            this.setOutputMode();
            this.awaitInput();
            if (this.aborted) {
                int n = -1;
                return n;
            }
            if (!this.buffer().hasRemaining() && this.endStream) {
                int n = -1;
                return n;
            }
            int chunk = Math.min(this.buffer().remaining(), len);
            this.buffer().get(b, off, chunk);
            this.capacityIncrement.addAndGet(chunk);
            if (!this.buffer().hasRemaining()) {
                this.incrementCapacity();
            }
            int n = chunk;
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    public void markEndStream() {
        if (this.endStream) {
            return;
        }
        this.lock.lock();
        try {
            if (!this.endStream) {
                this.endStream = true;
                this.capacityChannel = null;
                this.condition.signalAll();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
}

