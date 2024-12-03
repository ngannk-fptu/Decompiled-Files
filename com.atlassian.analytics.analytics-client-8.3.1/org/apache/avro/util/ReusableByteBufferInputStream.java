/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class ReusableByteBufferInputStream
extends InputStream {
    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
    private ByteBuffer byteBuffer = EMPTY_BUFFER;
    private Buffer buffer = this.byteBuffer;
    private int mark = 0;

    public void setByteBuffer(ByteBuffer buf) {
        this.byteBuffer = buf.duplicate();
        this.buffer = this.byteBuffer;
        this.mark = buf.position();
    }

    @Override
    public int read() throws IOException {
        if (this.buffer.hasRemaining()) {
            return this.byteBuffer.get() & 0xFF;
        }
        return -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.buffer.remaining() <= 0) {
            return -1;
        }
        int bytesToRead = Math.min(len, this.buffer.remaining());
        this.byteBuffer.get(b, off, bytesToRead);
        return bytesToRead;
    }

    @Override
    public long skip(long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        int bytesToSkip = n > (long)this.buffer.remaining() ? this.buffer.remaining() : (int)n;
        this.buffer.position(this.buffer.position() + bytesToSkip);
        return bytesToSkip;
    }

    @Override
    public synchronized void mark(int readLimit) {
        this.mark = this.buffer.position();
    }

    @Override
    public synchronized void reset() throws IOException {
        this.buffer.position(this.mark);
    }

    @Override
    public boolean markSupported() {
        return true;
    }
}

