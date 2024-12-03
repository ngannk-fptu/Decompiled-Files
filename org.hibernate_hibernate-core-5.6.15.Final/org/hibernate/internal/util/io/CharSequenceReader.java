/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.io;

import java.io.IOException;
import java.io.Reader;

public class CharSequenceReader
extends Reader {
    private CharSequence charSequence;
    private int length;
    private int position = 0;
    private int mark = 0;

    public CharSequenceReader(CharSequence charSequence) {
        this.charSequence = charSequence;
        this.length = charSequence.length();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        this.mark = this.position;
    }

    @Override
    public int read() throws IOException {
        this.verifyOpen();
        if (this.position >= this.length) {
            return -1;
        }
        return this.charSequence.charAt(this.position++);
    }

    protected void verifyOpen() throws IOException {
        if (this.charSequence == null) {
            throw new IOException("Stream closed");
        }
    }

    @Override
    public int read(char[] dest, int off, int len) throws IOException {
        this.verifyOpen();
        if (this.position >= this.length) {
            return -1;
        }
        if (off < 0 || off > dest.length || len < 0 || off + len > dest.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        int n = Math.min(this.length - this.position, len);
        for (int i = 0; i < n; ++i) {
            dest[i] = this.charSequence.charAt(n);
        }
        this.position += n;
        return n;
    }

    @Override
    public void reset() {
        this.position = this.mark;
    }

    @Override
    public long skip(long n) {
        if (n < 0L) {
            throw new IllegalArgumentException("Number of characters to skip must be greater than zero: " + n);
        }
        if (this.position >= this.length) {
            return -1L;
        }
        int dest = (int)Math.min((long)this.charSequence.length(), (long)this.position + n);
        int count = dest - this.position;
        this.position = dest;
        return count;
    }

    @Override
    public void close() throws IOException {
        this.charSequence = null;
        this.mark = 0;
        this.position = 0;
    }
}

