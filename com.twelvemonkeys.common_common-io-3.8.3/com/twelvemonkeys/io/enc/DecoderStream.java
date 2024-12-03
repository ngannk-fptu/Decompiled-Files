/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io.enc;

import com.twelvemonkeys.io.enc.Decoder;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class DecoderStream
extends FilterInputStream {
    private final ByteBuffer buffer;
    private final Decoder decoder;

    public DecoderStream(InputStream inputStream, Decoder decoder) {
        this(inputStream, decoder, 1024);
    }

    public DecoderStream(InputStream inputStream, Decoder decoder, int n) {
        super(inputStream);
        this.decoder = decoder;
        this.buffer = ByteBuffer.allocate(n);
        this.buffer.flip();
    }

    @Override
    public int available() throws IOException {
        return this.buffer.remaining();
    }

    @Override
    public int read() throws IOException {
        if (!this.buffer.hasRemaining() && this.fill() < 0) {
            return -1;
        }
        return this.buffer.get() & 0xFF;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        int n3;
        int n4;
        if (byArray == null) {
            throw new NullPointerException();
        }
        if (n < 0 || n > byArray.length || n2 < 0 || n + n2 > byArray.length || n + n2 < 0) {
            throw new IndexOutOfBoundsException("bytes.length=" + byArray.length + " offset=" + n + " length=" + n2);
        }
        if (n2 == 0) {
            return 0;
        }
        if (!this.buffer.hasRemaining() && this.fill() < 0) {
            return -1;
        }
        int n5 = n;
        for (n3 = 0; n2 > n3 && (this.buffer.hasRemaining() || this.fill() >= 0); n3 += n4) {
            n4 = Math.min(n2 - n3, this.buffer.remaining());
            this.buffer.get(byArray, n5, n4);
            n5 += n4;
        }
        return n3;
    }

    @Override
    public long skip(long l) throws IOException {
        long l2;
        int n;
        if (!this.buffer.hasRemaining() && this.fill() < 0) {
            return 0L;
        }
        for (l2 = 0L; l2 < l && (this.buffer.hasRemaining() || this.fill() >= 0); l2 += (long)n) {
            n = (int)Math.min(l - l2, (long)this.buffer.remaining());
            this.buffer.position(this.buffer.position() + n);
        }
        return l2;
    }

    private int fill() throws IOException {
        this.buffer.clear();
        int n = this.decoder.decode(this.in, this.buffer);
        if (n > this.buffer.capacity()) {
            throw new AssertionError((Object)String.format("Decode beyond buffer (%d): %d (using %s decoder)", this.buffer.capacity(), n, this.decoder.getClass().getName()));
        }
        this.buffer.flip();
        if (n == 0) {
            return -1;
        }
        return n;
    }
}

