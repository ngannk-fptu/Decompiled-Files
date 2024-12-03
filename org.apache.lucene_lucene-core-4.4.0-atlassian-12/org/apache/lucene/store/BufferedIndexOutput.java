/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.IOException;
import org.apache.lucene.store.IndexOutput;

public abstract class BufferedIndexOutput
extends IndexOutput {
    public static final int DEFAULT_BUFFER_SIZE = 16384;
    private final int bufferSize;
    private final byte[] buffer;
    private long bufferStart = 0L;
    private int bufferPosition = 0;

    public BufferedIndexOutput() {
        this(16384);
    }

    public BufferedIndexOutput(int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must be greater than 0 (got " + bufferSize + ")");
        }
        this.bufferSize = bufferSize;
        this.buffer = new byte[bufferSize];
    }

    @Override
    public void writeByte(byte b) throws IOException {
        if (this.bufferPosition >= this.bufferSize) {
            this.flush();
        }
        this.buffer[this.bufferPosition++] = b;
    }

    @Override
    public void writeBytes(byte[] b, int offset, int length) throws IOException {
        int bytesLeft = this.bufferSize - this.bufferPosition;
        if (bytesLeft >= length) {
            System.arraycopy(b, offset, this.buffer, this.bufferPosition, length);
            this.bufferPosition += length;
            if (this.bufferSize - this.bufferPosition == 0) {
                this.flush();
            }
        } else if (length > this.bufferSize) {
            if (this.bufferPosition > 0) {
                this.flush();
            }
            this.flushBuffer(b, offset, length);
            this.bufferStart += (long)length;
        } else {
            int pos = 0;
            while (pos < length) {
                int pieceLength = length - pos < bytesLeft ? length - pos : bytesLeft;
                System.arraycopy(b, pos + offset, this.buffer, this.bufferPosition, pieceLength);
                pos += pieceLength;
                this.bufferPosition += pieceLength;
                bytesLeft = this.bufferSize - this.bufferPosition;
                if (bytesLeft != 0) continue;
                this.flush();
                bytesLeft = this.bufferSize;
            }
        }
    }

    @Override
    public void flush() throws IOException {
        this.flushBuffer(this.buffer, this.bufferPosition);
        this.bufferStart += (long)this.bufferPosition;
        this.bufferPosition = 0;
    }

    private void flushBuffer(byte[] b, int len) throws IOException {
        this.flushBuffer(b, 0, len);
    }

    protected abstract void flushBuffer(byte[] var1, int var2, int var3) throws IOException;

    @Override
    public void close() throws IOException {
        this.flush();
    }

    @Override
    public long getFilePointer() {
        return this.bufferStart + (long)this.bufferPosition;
    }

    @Override
    public void seek(long pos) throws IOException {
        this.flush();
        this.bufferStart = pos;
    }

    @Override
    public abstract long length() throws IOException;

    public final int getBufferSize() {
        return this.bufferSize;
    }
}

