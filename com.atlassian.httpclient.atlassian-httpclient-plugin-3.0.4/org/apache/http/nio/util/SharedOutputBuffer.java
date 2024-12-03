/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.ContentOutputBuffer;
import org.apache.http.nio.util.ExpandableBuffer;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public class SharedOutputBuffer
extends ExpandableBuffer
implements ContentOutputBuffer {
    private final ReentrantLock lock;
    private final Condition condition;
    private volatile IOControl ioControl;
    private volatile boolean shutdown = false;
    private volatile boolean endOfStream = false;

    @Deprecated
    public SharedOutputBuffer(int bufferSize, IOControl ioControl, ByteBufferAllocator allocator) {
        super(bufferSize, allocator);
        Args.notNull(ioControl, "I/O content control");
        this.ioControl = ioControl;
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
    }

    public SharedOutputBuffer(int bufferSize, ByteBufferAllocator allocator) {
        super(bufferSize, allocator);
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
    }

    public SharedOutputBuffer(int bufferSize) {
        this(bufferSize, HeapByteBufferAllocator.INSTANCE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void reset() {
        if (this.shutdown) {
            return;
        }
        this.lock.lock();
        try {
            this.clear();
            this.endOfStream = false;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean hasData() {
        this.lock.lock();
        try {
            boolean bl = super.hasData();
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int available() {
        this.lock.lock();
        try {
            int n = super.available();
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
    public int capacity() {
        this.lock.lock();
        try {
            int n = super.capacity();
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
    public int length() {
        this.lock.lock();
        try {
            int n = super.length();
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    @Deprecated
    public int produceContent(ContentEncoder encoder) throws IOException {
        return this.produceContent(encoder, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int produceContent(ContentEncoder encoder, IOControl ioControl) throws IOException {
        if (this.shutdown) {
            return -1;
        }
        this.lock.lock();
        try {
            if (ioControl != null) {
                this.ioControl = ioControl;
            }
            this.setOutputMode();
            int bytesWritten = 0;
            if (super.hasData()) {
                bytesWritten = encoder.write(this.buffer);
                if (encoder.isCompleted()) {
                    this.endOfStream = true;
                }
            }
            if (!super.hasData()) {
                if (this.endOfStream && !encoder.isCompleted()) {
                    encoder.complete();
                }
                if (!this.endOfStream && this.ioControl != null) {
                    this.ioControl.suspendOutput();
                }
            }
            this.condition.signalAll();
            int n = bytesWritten;
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }

    public void close() {
        this.shutdown();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void shutdown() {
        if (this.shutdown) {
            return;
        }
        this.shutdown = true;
        this.lock.lock();
        try {
            this.condition.signalAll();
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            return;
        }
        int pos = off;
        this.lock.lock();
        try {
            Asserts.check(!this.shutdown && !this.endOfStream, "Buffer already closed for writing");
            this.setInputMode();
            int remaining = len;
            while (remaining > 0) {
                if (!this.buffer.hasRemaining()) {
                    this.flushContent();
                    this.setInputMode();
                }
                int chunk = Math.min(remaining, this.buffer.remaining());
                this.buffer.put(b, pos, chunk);
                remaining -= chunk;
                pos += chunk;
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public void write(byte[] b) throws IOException {
        if (b == null) {
            return;
        }
        this.write(b, 0, b.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(int b) throws IOException {
        this.lock.lock();
        try {
            Asserts.check(!this.shutdown && !this.endOfStream, "Buffer already closed for writing");
            this.setInputMode();
            if (!this.buffer.hasRemaining()) {
                this.flushContent();
                this.setInputMode();
            }
            this.buffer.put((byte)b);
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public void flush() throws IOException {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void flushContent() throws IOException {
        this.lock.lock();
        try {
            try {
                while (super.hasData()) {
                    if (this.shutdown) {
                        throw new InterruptedIOException("Output operation aborted");
                    }
                    if (this.ioControl != null) {
                        this.ioControl.requestOutput();
                    }
                    this.condition.await();
                }
            }
            catch (InterruptedException ex) {
                throw new IOException("Interrupted while flushing the content buffer");
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeCompleted() throws IOException {
        this.lock.lock();
        try {
            if (this.endOfStream) {
                return;
            }
            this.endOfStream = true;
            if (this.ioControl != null) {
                this.ioControl.requestOutput();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
}

