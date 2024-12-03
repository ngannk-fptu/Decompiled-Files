/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.util;

import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.stream.ImageInputStream;

class IIOInputStreamAdapter
extends InputStream {
    private ImageInputStream input;
    private final boolean hasLength;
    private long left;
    private long markPosition;

    public IIOInputStreamAdapter(ImageInputStream imageInputStream) {
        this(imageInputStream, -1L, false);
    }

    public IIOInputStreamAdapter(ImageInputStream imageInputStream, long l) {
        this(imageInputStream, l, true);
    }

    private IIOInputStreamAdapter(ImageInputStream imageInputStream, long l, boolean bl) {
        Validate.notNull((Object)imageInputStream, (String)"stream");
        Validate.isTrue((!bl || l >= 0L ? 1 : 0) != 0, (Object)l, (String)"length < 0: %d");
        this.input = imageInputStream;
        this.left = l;
        this.hasLength = bl;
    }

    @Override
    public void close() throws IOException {
        if (this.hasLength) {
            this.input.seek(this.input.getStreamPosition() + this.left);
        }
        this.left = 0L;
        this.input = null;
    }

    @Override
    public int available() throws IOException {
        if (this.hasLength) {
            return this.left > 0L ? (int)Math.min(Integer.MAX_VALUE, this.left) : 0;
        }
        return 0;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int n) {
        try {
            this.markPosition = this.input.getStreamPosition();
        }
        catch (IOException iOException) {
            throw new IllegalStateException("Could not read stream position: " + iOException.getMessage(), iOException);
        }
    }

    @Override
    public void reset() throws IOException {
        long l = this.input.getStreamPosition() - this.markPosition;
        this.input.seek(this.markPosition);
        this.left += l;
    }

    @Override
    public int read() throws IOException {
        if (this.hasLength && this.left-- <= 0L) {
            this.left = 0L;
            return -1;
        }
        return this.input.read();
    }

    @Override
    public final int read(byte[] byArray) throws IOException {
        return this.read(byArray, 0, byArray.length);
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        if (this.hasLength && this.left <= 0L) {
            return -1;
        }
        int n3 = this.input.read(byArray, n, (int)this.findMaxLen(n2));
        if (this.hasLength) {
            this.left = n3 < 0 ? 0L : this.left - (long)n3;
        }
        return n3;
    }

    private long findMaxLen(long l) {
        if (this.hasLength && this.left < l) {
            return Math.max(this.left, 0L);
        }
        return Math.max(l, 0L);
    }

    @Override
    public long skip(long l) throws IOException {
        long l2 = this.input.skipBytes(this.findMaxLen(l));
        this.left -= l2;
        return l2;
    }
}

