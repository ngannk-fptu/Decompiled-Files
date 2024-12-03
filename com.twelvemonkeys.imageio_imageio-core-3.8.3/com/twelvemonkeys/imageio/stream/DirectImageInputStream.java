/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.stream.ImageInputStreamImpl;

public final class DirectImageInputStream
extends ImageInputStreamImpl {
    private final InputStream stream;
    private final long length;

    public DirectImageInputStream(InputStream inputStream) {
        this(inputStream, -1L);
    }

    public DirectImageInputStream(InputStream inputStream, long l) {
        this.stream = (InputStream)Validate.notNull((Object)inputStream, (String)"stream");
        this.length = (Long)Validate.isTrue((l >= 0L || l == -1L ? 1 : 0) != 0, (Object)l, (String)"negative length: %d");
    }

    @Override
    public int read() throws IOException {
        this.bitOffset = 0;
        ++this.streamPos;
        return this.stream.read();
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        this.bitOffset = 0;
        int n3 = this.stream.read(byArray, n, n2);
        if (n3 > 0) {
            this.streamPos += (long)n3;
        }
        return n3;
    }

    @Override
    public void seek(long l) throws IOException {
        long l2;
        this.checkClosed();
        if (l < this.streamPos) {
            throw new IndexOutOfBoundsException("pos < flushedPos");
        }
        this.bitOffset = 0;
        while (this.streamPos < l && (l2 = this.stream.skip(l - this.streamPos)) > 0L) {
            this.streamPos += l2;
        }
    }

    @Override
    public long getFlushedPosition() {
        return this.streamPos;
    }

    @Override
    public long length() {
        return this.length;
    }

    @Override
    public int readBit() throws IOException {
        throw new UnsupportedOperationException("Bit reading not supported");
    }

    @Override
    public long readBits(int n) throws IOException {
        throw new UnsupportedOperationException("Bit reading not supported");
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
        super.close();
    }
}

