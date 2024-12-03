/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.transport.http;

import java.io.IOException;
import java.io.InputStream;

public class NonBlockingBufferedInputStream
extends InputStream {
    private InputStream in;
    private int remainingContent = Integer.MAX_VALUE;
    private byte[] buffer = new byte[4096];
    private int offset = 0;
    private int numbytes = 0;

    public void setInputStream(InputStream in) {
        this.in = in;
        this.numbytes = 0;
        this.offset = 0;
        this.remainingContent = in == null ? 0 : Integer.MAX_VALUE;
    }

    public void setContentLength(int value) {
        if (this.in != null) {
            this.remainingContent = value - (this.numbytes - this.offset);
        }
    }

    private void refillBuffer() throws IOException {
        if (this.remainingContent <= 0 || this.in == null) {
            return;
        }
        this.numbytes = this.in.available();
        if (this.numbytes > this.remainingContent) {
            this.numbytes = this.remainingContent;
        }
        if (this.numbytes > this.buffer.length) {
            this.numbytes = this.buffer.length;
        }
        if (this.numbytes <= 0) {
            this.numbytes = 1;
        }
        this.numbytes = this.in.read(this.buffer, 0, this.numbytes);
        this.remainingContent -= this.numbytes;
        this.offset = 0;
    }

    public int read() throws IOException {
        if (this.in == null) {
            return -1;
        }
        if (this.offset >= this.numbytes) {
            this.refillBuffer();
        }
        if (this.offset >= this.numbytes) {
            return -1;
        }
        return this.buffer[this.offset++] & 0xFF;
    }

    public int read(byte[] dest) throws IOException {
        return this.read(dest, 0, dest.length);
    }

    public int read(byte[] dest, int off, int len) throws IOException {
        int ready = this.numbytes - this.offset;
        if (ready >= len) {
            System.arraycopy(this.buffer, this.offset, dest, off, len);
            this.offset += len;
            return len;
        }
        if (ready > 0) {
            System.arraycopy(this.buffer, this.offset, dest, off, ready);
            this.offset = this.numbytes;
            return ready;
        }
        if (this.in == null) {
            return -1;
        }
        this.refillBuffer();
        if (this.offset >= this.numbytes) {
            return -1;
        }
        return this.read(dest, off, len);
    }

    public int skip(int len) throws IOException {
        int count = 0;
        while (len-- > 0 && this.read() >= 0) {
            ++count;
        }
        return count;
    }

    public int available() throws IOException {
        if (this.in == null) {
            return 0;
        }
        return this.numbytes - this.offset + this.in.available();
    }

    public void close() throws IOException {
        this.setInputStream(null);
    }

    public int peek() throws IOException {
        if (this.in == null) {
            return -1;
        }
        if (this.offset >= this.numbytes) {
            this.refillBuffer();
        }
        if (this.offset >= this.numbytes) {
            return -1;
        }
        return this.buffer[this.offset] & 0xFF;
    }
}

