/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.io;

import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream
extends InputStream {
    static final int BUFFER_SIZE = 4096;
    final InputStream in;
    final int size;
    int left;

    public LimitedInputStream(InputStream in, int size) {
        this.in = in;
        this.left = size;
        this.size = size;
    }

    @Override
    public int read() throws IOException {
        if (this.left <= 0) {
            this.eof();
            return -1;
        }
        --this.left;
        return this.in.read();
    }

    @Override
    public int available() throws IOException {
        return Math.min(this.left, this.in.available());
    }

    @Override
    public void close() throws IOException {
        this.eof();
        this.in.close();
    }

    protected void eof() {
    }

    @Override
    public void mark(int readlimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int min = Math.min(len, this.left);
        if (min == 0) {
            return 0;
        }
        int read = this.in.read(b, off, min);
        if (read > 0) {
            this.left -= read;
        }
        return read;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public void reset() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long skip(long n) throws IOException {
        long count = 0L;
        byte[] buffer = new byte[4096];
        while (n > 0L && this.read() >= 0) {
            int size = this.read(buffer);
            if (size <= 0) {
                return count;
            }
            count += (long)size;
            n -= (long)size;
        }
        return count;
    }
}

