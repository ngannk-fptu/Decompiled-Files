/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.ChunkedInputStream;

public class ContentLengthInputStream
extends InputStream {
    private long contentLength;
    private long pos = 0L;
    private boolean closed = false;
    private InputStream wrappedStream = null;

    public ContentLengthInputStream(InputStream in, int contentLength) {
        this(in, (long)contentLength);
    }

    public ContentLengthInputStream(InputStream in, long contentLength) {
        this.wrappedStream = in;
        this.contentLength = contentLength;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        if (!this.closed) {
            try {
                ChunkedInputStream.exhaustInputStream(this);
            }
            finally {
                this.closed = true;
            }
        }
    }

    @Override
    public int read() throws IOException {
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        }
        if (this.pos >= this.contentLength) {
            return -1;
        }
        ++this.pos;
        return this.wrappedStream.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        }
        if (this.pos >= this.contentLength) {
            return -1;
        }
        if (this.pos + (long)len > this.contentLength) {
            len = (int)(this.contentLength - this.pos);
        }
        int count = this.wrappedStream.read(b, off, len);
        this.pos += (long)count;
        return count;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public long skip(long n) throws IOException {
        long length = Math.min(n, this.contentLength - this.pos);
        if ((length = this.wrappedStream.skip(length)) > 0L) {
            this.pos += length;
        }
        return length;
    }

    @Override
    public int available() throws IOException {
        if (this.closed) {
            return 0;
        }
        int avail = this.wrappedStream.available();
        if (this.pos + (long)avail > this.contentLength) {
            avail = (int)(this.contentLength - this.pos);
        }
        return avail;
    }
}

