/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

class ServerInputStream
extends InputStream {
    private long available = -1L;
    private long markedAvailable;
    private BufferedInputStream in;

    public ServerInputStream(BufferedInputStream in, int available) {
        this.in = in;
        this.available = available;
    }

    public int read() throws IOException {
        if (this.available > 0L) {
            --this.available;
            return this.in.read();
        }
        if (this.available == -1L) {
            return this.in.read();
        }
        return -1;
    }

    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.available > 0L) {
            int read;
            if ((long)len > this.available) {
                len = (int)this.available;
            }
            this.available = (read = this.in.read(b, off, len)) != -1 ? (this.available -= (long)read) : -1L;
            return read;
        }
        if (this.available == -1L) {
            return this.in.read(b, off, len);
        }
        return -1;
    }

    public long skip(long n) throws IOException {
        long skip = this.in.skip(n);
        if (this.available > 0L) {
            this.available -= skip;
        }
        return skip;
    }

    public void mark(int readlimit) {
        this.in.mark(readlimit);
        this.markedAvailable = this.available;
    }

    public void reset() throws IOException {
        this.in.reset();
        this.available = this.markedAvailable;
    }

    public boolean markSupported() {
        return true;
    }
}

