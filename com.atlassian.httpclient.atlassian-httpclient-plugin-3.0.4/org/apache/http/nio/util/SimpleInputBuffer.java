/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.util;

import java.io.IOException;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.ContentInputBuffer;
import org.apache.http.nio.util.ExpandableBuffer;
import org.apache.http.nio.util.HeapByteBufferAllocator;

public class SimpleInputBuffer
extends ExpandableBuffer
implements ContentInputBuffer {
    private boolean endOfStream = false;

    public SimpleInputBuffer(int bufferSize, ByteBufferAllocator allocator) {
        super(bufferSize, allocator);
    }

    public SimpleInputBuffer(int bufferSize) {
        this(bufferSize, HeapByteBufferAllocator.INSTANCE);
    }

    @Override
    public void reset() {
        this.endOfStream = false;
        super.clear();
    }

    @Override
    public int consumeContent(ContentDecoder decoder) throws IOException {
        int bytesRead;
        this.setInputMode();
        int totalRead = 0;
        while ((bytesRead = decoder.read(this.buffer)) != -1) {
            if (bytesRead == 0) {
                if (this.buffer.hasRemaining()) break;
                this.expand();
                continue;
            }
            totalRead += bytesRead;
        }
        if (bytesRead == -1 || decoder.isCompleted()) {
            this.endOfStream = true;
        }
        return totalRead;
    }

    public boolean isEndOfStream() {
        return !this.hasData() && this.endOfStream;
    }

    @Override
    public int read() throws IOException {
        if (this.isEndOfStream()) {
            return -1;
        }
        this.setOutputMode();
        return this.buffer.get() & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.isEndOfStream()) {
            return -1;
        }
        if (b == null) {
            return 0;
        }
        this.setOutputMode();
        int chunk = len;
        if (chunk > this.buffer.remaining()) {
            chunk = this.buffer.remaining();
        }
        this.buffer.get(b, off, chunk);
        return chunk;
    }

    public int read(byte[] b) throws IOException {
        if (this.isEndOfStream()) {
            return -1;
        }
        if (b == null) {
            return 0;
        }
        return this.read(b, 0, b.length);
    }

    public void shutdown() {
        this.endOfStream = true;
    }
}

