/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.ConnectionClosedException;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.util.Args;

public class ContentLengthInputStream
extends InputStream {
    private static final int BUFFER_SIZE = 2048;
    private final long contentLength;
    private long pos = 0L;
    private boolean closed = false;
    private SessionInputBuffer in = null;

    public ContentLengthInputStream(SessionInputBuffer in, long contentLength) {
        this.in = Args.notNull(in, "Session input buffer");
        this.contentLength = Args.notNegative(contentLength, "Content length");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        if (!this.closed) {
            try {
                if (this.pos < this.contentLength) {
                    byte[] buffer = new byte[2048];
                    while (this.read(buffer) >= 0) {
                    }
                }
            }
            finally {
                this.closed = true;
            }
        }
    }

    @Override
    public int available() throws IOException {
        if (this.in instanceof BufferInfo) {
            int len = ((BufferInfo)((Object)this.in)).length();
            return Math.min(len, (int)(this.contentLength - this.pos));
        }
        return 0;
    }

    @Override
    public int read() throws IOException {
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        }
        if (this.pos >= this.contentLength) {
            return -1;
        }
        int b = this.in.read();
        if (b == -1) {
            if (this.pos < this.contentLength) {
                throw new ConnectionClosedException("Premature end of Content-Length delimited message body (expected: " + this.contentLength + "; received: " + this.pos);
            }
        } else {
            ++this.pos;
        }
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count;
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        }
        if (this.pos >= this.contentLength) {
            return -1;
        }
        int chunk = len;
        if (this.pos + (long)len > this.contentLength) {
            chunk = (int)(this.contentLength - this.pos);
        }
        if ((count = this.in.read(b, off, chunk)) == -1 && this.pos < this.contentLength) {
            throw new ConnectionClosedException("Premature end of Content-Length delimited message body (expected: " + this.contentLength + "; received: " + this.pos);
        }
        if (count > 0) {
            this.pos += (long)count;
        }
        return count;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public long skip(long n) throws IOException {
        int l;
        if (n <= 0L) {
            return 0L;
        }
        byte[] buffer = new byte[2048];
        long count = 0L;
        for (long remaining = Math.min(n, this.contentLength - this.pos); remaining > 0L && (l = this.read(buffer, 0, (int)Math.min(2048L, remaining))) != -1; remaining -= (long)l) {
            count += (long)l;
        }
        return count;
    }
}

