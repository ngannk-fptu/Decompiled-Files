/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

public class ByteBufferInputStream
extends InputStream {
    private List<ByteBuffer> buffers;
    private int current;

    public ByteBufferInputStream(List<ByteBuffer> buffers) {
        this.buffers = buffers;
    }

    @Override
    public int read() throws IOException {
        ByteBuffer buffer = this.getBuffer();
        if (buffer == null) {
            return -1;
        }
        return buffer.get() & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        ByteBuffer buffer = this.getBuffer();
        if (buffer == null) {
            return -1;
        }
        int remaining = buffer.remaining();
        if (len > remaining) {
            buffer.get(b, off, remaining);
            return remaining;
        }
        buffer.get(b, off, len);
        return len;
    }

    public ByteBuffer readBuffer(int length) throws IOException {
        if (length == 0) {
            return ByteBuffer.allocate(0);
        }
        ByteBuffer buffer = this.getBuffer();
        if (buffer == null) {
            return ByteBuffer.allocate(0);
        }
        if (buffer.remaining() == length) {
            ++this.current;
            return buffer;
        }
        ByteBuffer result = ByteBuffer.allocate(length);
        for (int start = 0; start < length; start += this.read(result.array(), start, length - start)) {
        }
        return result;
    }

    private ByteBuffer getBuffer() throws IOException {
        while (this.current < this.buffers.size()) {
            ByteBuffer buffer = this.buffers.get(this.current);
            if (buffer.hasRemaining()) {
                return buffer;
            }
            ++this.current;
        }
        return null;
    }
}

