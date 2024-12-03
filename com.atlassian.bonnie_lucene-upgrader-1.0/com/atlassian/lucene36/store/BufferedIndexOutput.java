/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.IndexOutput;
import java.io.IOException;

public abstract class BufferedIndexOutput
extends IndexOutput {
    static final int BUFFER_SIZE = 16384;
    private final byte[] buffer = new byte[16384];
    private long bufferStart = 0L;
    private int bufferPosition = 0;

    public void writeByte(byte b) throws IOException {
        if (this.bufferPosition >= 16384) {
            this.flush();
        }
        this.buffer[this.bufferPosition++] = b;
    }

    public void writeBytes(byte[] b, int offset, int length) throws IOException {
        int bytesLeft = 16384 - this.bufferPosition;
        if (bytesLeft >= length) {
            System.arraycopy(b, offset, this.buffer, this.bufferPosition, length);
            this.bufferPosition += length;
            if (16384 - this.bufferPosition == 0) {
                this.flush();
            }
        } else if (length > 16384) {
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
                bytesLeft = 16384 - this.bufferPosition;
                if (bytesLeft != 0) continue;
                this.flush();
                bytesLeft = 16384;
            }
        }
    }

    public void flush() throws IOException {
        this.flushBuffer(this.buffer, this.bufferPosition);
        this.bufferStart += (long)this.bufferPosition;
        this.bufferPosition = 0;
    }

    private void flushBuffer(byte[] b, int len) throws IOException {
        this.flushBuffer(b, 0, len);
    }

    protected abstract void flushBuffer(byte[] var1, int var2, int var3) throws IOException;

    public void close() throws IOException {
        this.flush();
    }

    public long getFilePointer() {
        return this.bufferStart + (long)this.bufferPosition;
    }

    public void seek(long pos) throws IOException {
        this.flush();
        this.bufferStart = pos;
    }

    public abstract long length() throws IOException;
}

