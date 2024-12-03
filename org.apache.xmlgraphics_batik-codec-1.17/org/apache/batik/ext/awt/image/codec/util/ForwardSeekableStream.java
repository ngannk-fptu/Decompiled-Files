/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.codec.util;

import java.io.IOException;
import java.io.InputStream;
import org.apache.batik.ext.awt.image.codec.util.SeekableStream;

public class ForwardSeekableStream
extends SeekableStream {
    private InputStream src;
    long pointer = 0L;

    public ForwardSeekableStream(InputStream src) {
        this.src = src;
    }

    @Override
    public final int read() throws IOException {
        int result = this.src.read();
        if (result != -1) {
            ++this.pointer;
        }
        return result;
    }

    @Override
    public final int read(byte[] b, int off, int len) throws IOException {
        int result = this.src.read(b, off, len);
        if (result != -1) {
            this.pointer += (long)result;
        }
        return result;
    }

    @Override
    public final long skip(long n) throws IOException {
        long skipped = this.src.skip(n);
        this.pointer += skipped;
        return skipped;
    }

    @Override
    public final int available() throws IOException {
        return this.src.available();
    }

    @Override
    public final void close() throws IOException {
        this.src.close();
    }

    @Override
    public final synchronized void mark(int readLimit) {
        this.markPos = this.pointer;
        this.src.mark(readLimit);
    }

    @Override
    public final synchronized void reset() throws IOException {
        if (this.markPos != -1L) {
            this.pointer = this.markPos;
        }
        this.src.reset();
    }

    @Override
    public boolean markSupported() {
        return this.src.markSupported();
    }

    @Override
    public final boolean canSeekBackwards() {
        return false;
    }

    @Override
    public final long getFilePointer() {
        return this.pointer;
    }

    @Override
    public final void seek(long pos) throws IOException {
        while (pos - this.pointer > 0L) {
            this.pointer += this.src.skip(pos - this.pointer);
        }
    }
}

