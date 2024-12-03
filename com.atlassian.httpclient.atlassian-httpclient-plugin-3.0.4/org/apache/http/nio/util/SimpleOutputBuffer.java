/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.util;

import java.io.IOException;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.ContentOutputBuffer;
import org.apache.http.nio.util.ExpandableBuffer;
import org.apache.http.nio.util.HeapByteBufferAllocator;

public class SimpleOutputBuffer
extends ExpandableBuffer
implements ContentOutputBuffer {
    private boolean endOfStream = false;

    public SimpleOutputBuffer(int bufferSize, ByteBufferAllocator allocator) {
        super(bufferSize, allocator);
    }

    public SimpleOutputBuffer(int bufferSize) {
        this(bufferSize, HeapByteBufferAllocator.INSTANCE);
    }

    @Override
    public int produceContent(ContentEncoder encoder) throws IOException {
        this.setOutputMode();
        int bytesWritten = encoder.write(this.buffer);
        if (!this.hasData() && this.endOfStream) {
            encoder.complete();
        }
        return bytesWritten;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            return;
        }
        if (this.endOfStream) {
            return;
        }
        this.setInputMode();
        this.ensureCapacity(this.buffer.position() + len);
        this.buffer.put(b, off, len);
    }

    public void write(byte[] b) throws IOException {
        if (b == null) {
            return;
        }
        if (this.endOfStream) {
            return;
        }
        this.write(b, 0, b.length);
    }

    @Override
    public void write(int b) throws IOException {
        if (this.endOfStream) {
            return;
        }
        this.setInputMode();
        this.ensureCapacity(this.capacity() + 1);
        this.buffer.put((byte)b);
    }

    @Override
    public void reset() {
        super.clear();
        this.endOfStream = false;
    }

    @Override
    public void flush() {
    }

    @Override
    public void writeCompleted() {
        this.endOfStream = true;
    }

    public void shutdown() {
        this.endOfStream = true;
    }
}

