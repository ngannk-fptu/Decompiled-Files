/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.io.ProxyInputStream;

public class CountingInputStream
extends ProxyInputStream {
    private long count;

    public CountingInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read(byte[] b) throws IOException {
        int found = super.read(b);
        this.count += found >= 0 ? (long)found : 0L;
        return found;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int found = super.read(b, off, len);
        this.count += found >= 0 ? (long)found : 0L;
        return found;
    }

    @Override
    public int read() throws IOException {
        int found = super.read();
        this.count += found >= 0 ? 1L : 0L;
        return found;
    }

    @Override
    public long skip(long length) throws IOException {
        long skip = super.skip(length);
        this.count += skip;
        return skip;
    }

    public synchronized int getCount() {
        long result = this.getByteCount();
        if (result > Integer.MAX_VALUE) {
            throw new ArithmeticException("The byte count " + result + " is too large to be converted to an int");
        }
        return (int)result;
    }

    public synchronized int resetCount() {
        long result = this.resetByteCount();
        if (result > Integer.MAX_VALUE) {
            throw new ArithmeticException("The byte count " + result + " is too large to be converted to an int");
        }
        return (int)result;
    }

    public synchronized long getByteCount() {
        return this.count;
    }

    public synchronized long resetByteCount() {
        long tmp = this.count;
        this.count = 0L;
        return tmp;
    }

    public String toString() {
        return "Tika Counting InputStream wrapping " + this.in.toString();
    }
}

