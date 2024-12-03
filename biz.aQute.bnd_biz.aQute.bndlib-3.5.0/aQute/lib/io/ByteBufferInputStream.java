/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream
extends InputStream {
    private final ByteBuffer bb;

    public ByteBufferInputStream(ByteBuffer buffer) {
        buffer.mark();
        this.bb = buffer;
    }

    @Override
    public int read() throws IOException {
        if (!this.bb.hasRemaining()) {
            return -1;
        }
        return 0xFF & this.bb.get();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int remaining = this.bb.remaining();
        if (remaining <= 0) {
            return -1;
        }
        int length = Math.min(len, remaining);
        this.bb.get(b, off, length);
        return length;
    }

    @Override
    public long skip(long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        int skipped = Math.min((int)n, this.bb.remaining());
        this.bb.position(this.bb.position() + skipped);
        return skipped;
    }

    @Override
    public int available() throws IOException {
        return this.bb.remaining();
    }

    @Override
    public void close() throws IOException {
        this.bb.position(this.bb.limit());
    }

    @Override
    public void mark(int readlimit) {
        this.bb.mark();
    }

    @Override
    public void reset() throws IOException {
        this.bb.reset();
    }

    @Override
    public boolean markSupported() {
        return true;
    }
}

