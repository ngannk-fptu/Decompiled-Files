/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.transport.http;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ChunkedInputStream
extends FilterInputStream {
    protected long chunkSize = 0L;
    protected volatile boolean closed = false;
    private static final int maxCharLong = Long.toHexString(Long.MAX_VALUE).toString().length();
    private byte[] buf = new byte[maxCharLong + 2];

    private ChunkedInputStream() {
        super(null);
    }

    public ChunkedInputStream(InputStream is) {
        super(is);
    }

    public synchronized int read() throws IOException {
        if (this.closed) {
            return -1;
        }
        try {
            if (this.chunkSize < 1L && 0L == this.getChunked()) {
                return -1;
            }
            int rc = this.in.read();
            if (rc > 0) {
                --this.chunkSize;
            }
            return rc;
        }
        catch (IOException e) {
            this.closed = true;
            throw e;
        }
    }

    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    public synchronized int read(byte[] b, int off, int len) throws IOException {
        if (this.closed) {
            return -1;
        }
        int totalread = 0;
        int bytesread = 0;
        try {
            do {
                if (this.chunkSize < 1L && 0L == this.getChunked()) {
                    if (totalread == 0) {
                        return -1;
                    }
                    return totalread;
                }
                bytesread = this.in.read(b, off + totalread, Math.min(len - totalread, (int)Math.min(this.chunkSize, Integer.MAX_VALUE)));
                if (bytesread <= 0) continue;
                totalread += bytesread;
                this.chunkSize -= (long)bytesread;
            } while (len - totalread > 0 && bytesread > -1);
        }
        catch (IOException e) {
            this.closed = true;
            throw e;
        }
        return totalread;
    }

    public long skip(long n) throws IOException {
        if (this.closed) {
            return 0L;
        }
        long skipped = 0L;
        byte[] b = new byte[1024];
        int bread = -1;
        do {
            if ((bread = this.read(b, 0, b.length)) <= 0) continue;
            skipped += (long)bread;
        } while (bread != -1 && skipped < n);
        return skipped;
    }

    public int available() throws IOException {
        if (this.closed) {
            return 0;
        }
        int rc = (int)Math.min(this.chunkSize, Integer.MAX_VALUE);
        return Math.min(rc, this.in.available());
    }

    protected long getChunked() throws IOException {
        int bufsz = 0;
        this.chunkSize = -1L;
        int c = -1;
        do {
            if ((c = this.in.read()) <= -1 || c == 13 || c == 10 || c == 32 || c == 9) continue;
            this.buf[bufsz++] = (byte)c;
        } while (c > -1 && (c != 10 || bufsz == 0) && bufsz < this.buf.length);
        if (c < 0) {
            this.closed = true;
        }
        String sbuf = new String(this.buf, 0, bufsz);
        if (bufsz > maxCharLong) {
            this.closed = true;
            throw new IOException("Chunked input stream failed to receive valid chunk size:" + sbuf);
        }
        try {
            this.chunkSize = Long.parseLong(sbuf, 16);
        }
        catch (NumberFormatException ne) {
            this.closed = true;
            throw new IOException("'" + sbuf + "' " + ne.getMessage());
        }
        if (this.chunkSize < 0L) {
            this.closed = true;
        }
        if (this.chunkSize == 0L) {
            this.closed = true;
            if (this.in.read() != -1) {
                this.in.read();
            }
        }
        if (this.chunkSize != 0L && c < 0) {
            throw new IOException("HTTP Chunked stream closed in middle of chunk.");
        }
        if (this.chunkSize < 0L) {
            throw new IOException("HTTP Chunk size received " + this.chunkSize + " is less than zero.");
        }
        return this.chunkSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() throws IOException {
        ChunkedInputStream chunkedInputStream = this;
        synchronized (chunkedInputStream) {
            if (this.closed) {
                return;
            }
            this.closed = true;
        }
        byte[] b = new byte[1024];
        int bread = -1;
        while ((bread = this.read(b, 0, b.length)) != -1) {
        }
    }

    public void reset() throws IOException {
        throw new IOException("Don't support marked streams");
    }

    public boolean markSupported() {
        return false;
    }
}

