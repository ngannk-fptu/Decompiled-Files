/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io.enc;

import com.twelvemonkeys.io.enc.Encoder;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class EncoderStream
extends FilterOutputStream {
    private final Encoder encoder;
    private final boolean flushOnWrite;
    private final ByteBuffer buffer;

    public EncoderStream(OutputStream outputStream, Encoder encoder) {
        this(outputStream, encoder, false);
    }

    public EncoderStream(OutputStream outputStream, Encoder encoder, boolean bl) {
        super(outputStream);
        this.encoder = encoder;
        this.flushOnWrite = bl;
        this.buffer = ByteBuffer.allocate(1024);
    }

    @Override
    public void close() throws IOException {
        this.flush();
        super.close();
    }

    @Override
    public void flush() throws IOException {
        this.encodeBuffer();
        super.flush();
    }

    private void encodeBuffer() throws IOException {
        if (this.buffer.position() != 0) {
            this.buffer.flip();
            this.encoder.encode(this.out, this.buffer);
            this.buffer.clear();
        }
    }

    @Override
    public void write(byte[] byArray) throws IOException {
        this.write(byArray, 0, byArray.length);
    }

    @Override
    public void write(byte[] byArray, int n, int n2) throws IOException {
        if (!this.flushOnWrite && n2 < this.buffer.remaining()) {
            this.buffer.put(byArray, n, n2);
        } else {
            this.encodeBuffer();
            this.encoder.encode(this.out, ByteBuffer.wrap(byArray, n, n2));
        }
    }

    @Override
    public void write(int n) throws IOException {
        if (!this.buffer.hasRemaining()) {
            this.encodeBuffer();
        }
        this.buffer.put((byte)n);
    }
}

