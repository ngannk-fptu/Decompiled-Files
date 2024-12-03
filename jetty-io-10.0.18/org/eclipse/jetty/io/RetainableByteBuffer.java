/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.NanoTime
 *  org.eclipse.jetty.util.Retainable
 */
package org.eclipse.jetty.io;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.Retainable;

public class RetainableByteBuffer
implements Retainable {
    private final ByteBuffer buffer;
    private final AtomicInteger references = new AtomicInteger();
    private final Consumer<RetainableByteBuffer> releaser;
    private final AtomicLong lastUpdate = new AtomicLong(NanoTime.now());

    RetainableByteBuffer(ByteBuffer buffer, Consumer<RetainableByteBuffer> releaser) {
        this.releaser = releaser;
        this.buffer = buffer;
    }

    public int capacity() {
        return this.buffer.capacity();
    }

    public ByteBuffer getBuffer() {
        return this.buffer;
    }

    public long getLastUpdate() {
        return this.lastUpdate.getOpaque();
    }

    public boolean isRetained() {
        return this.references.get() > 1;
    }

    public boolean isDirect() {
        return this.buffer.isDirect();
    }

    void acquire() {
        if (this.references.getAndUpdate(c -> c == 0 ? 1 : c) != 0) {
            throw new IllegalStateException("re-pooled while still used " + this);
        }
    }

    public void retain() {
        if (this.references.getAndUpdate(c -> c == 0 ? 0 : c + 1) == 0) {
            throw new IllegalStateException("released " + this);
        }
    }

    public boolean release() {
        int ref = this.references.updateAndGet(c -> {
            if (c == 0) {
                throw new IllegalStateException("already released " + this);
            }
            return c - 1;
        });
        if (ref == 0) {
            this.lastUpdate.setOpaque(NanoTime.now());
            this.releaser.accept(this);
            return true;
        }
        return false;
    }

    public int remaining() {
        return this.buffer.remaining();
    }

    public boolean hasRemaining() {
        return this.remaining() > 0;
    }

    public boolean isEmpty() {
        return !this.hasRemaining();
    }

    public void clear() {
        BufferUtil.clear((ByteBuffer)this.buffer);
    }

    public String toString() {
        return String.format("%s@%x{%s,r=%d}", this.getClass().getSimpleName(), this.hashCode(), BufferUtil.toDetailString((ByteBuffer)this.buffer), this.references.get());
    }
}

