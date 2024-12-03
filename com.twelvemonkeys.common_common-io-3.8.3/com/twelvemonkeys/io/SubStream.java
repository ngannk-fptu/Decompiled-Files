/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.lang.Validate;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class SubStream
extends FilterInputStream {
    private long bytesLeft;
    private int markLimit;

    public SubStream(InputStream inputStream, long l) {
        super((InputStream)Validate.notNull((Object)inputStream, (String)"stream"));
        this.bytesLeft = l;
    }

    @Override
    public void close() throws IOException {
        while (this.bytesLeft > 0L) {
            this.skip(this.bytesLeft);
        }
    }

    @Override
    public int available() throws IOException {
        return (int)Math.min((long)super.available(), this.bytesLeft);
    }

    @Override
    public void mark(int n) {
        super.mark(n);
        this.markLimit = n;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        this.bytesLeft += (long)this.markLimit;
    }

    @Override
    public int read() throws IOException {
        if (this.bytesLeft-- <= 0L) {
            return -1;
        }
        return super.read();
    }

    @Override
    public final int read(byte[] byArray) throws IOException {
        return this.read(byArray, 0, byArray.length);
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        if (this.bytesLeft <= 0L) {
            return -1;
        }
        int n3 = super.read(byArray, n, (int)this.findMaxLen(n2));
        this.bytesLeft = n3 < 0 ? 0L : this.bytesLeft - (long)n3;
        return n3;
    }

    private long findMaxLen(long l) {
        if (this.bytesLeft < l) {
            return (int)Math.max(this.bytesLeft, 0L);
        }
        return l;
    }

    @Override
    public long skip(long l) throws IOException {
        long l2 = super.skip(this.findMaxLen(l));
        this.bytesLeft -= l2;
        return l2;
    }
}

