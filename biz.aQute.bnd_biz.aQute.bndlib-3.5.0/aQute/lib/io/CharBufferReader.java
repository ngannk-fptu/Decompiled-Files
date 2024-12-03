/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

public class CharBufferReader
extends Reader {
    private final CharBuffer cb;

    public CharBufferReader(CharBuffer buffer) {
        buffer.mark();
        this.cb = buffer;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int remaining = this.cb.remaining();
        if (remaining <= 0) {
            return -1;
        }
        int length = Math.min(len, remaining);
        this.cb.get(cbuf, off, length);
        return length;
    }

    @Override
    public void close() throws IOException {
        this.cb.position(this.cb.limit());
    }

    @Override
    public int read() throws IOException {
        if (!this.cb.hasRemaining()) {
            return -1;
        }
        return this.cb.get();
    }

    @Override
    public long skip(long n) throws IOException {
        if (n < 0L) {
            return 0L;
        }
        int skipped = Math.min((int)n, this.cb.remaining());
        this.cb.position(this.cb.position() + skipped);
        return skipped;
    }

    @Override
    public boolean ready() throws IOException {
        return true;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        this.cb.mark();
    }

    @Override
    public void reset() throws IOException {
        this.cb.reset();
    }
}

