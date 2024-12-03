/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.SeekableStream;
import java.io.IOException;

class NoEOFStream
extends SeekableStream {
    private SeekableStream stream;

    NoEOFStream(SeekableStream ss) {
        if (ss == null) {
            throw new IllegalArgumentException();
        }
        this.stream = ss;
    }

    public int read() throws IOException {
        int b = this.stream.read();
        return b < 0 ? 0 : b;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int count = this.stream.read(b, off, len);
        return count < 0 ? len : count;
    }

    public long getFilePointer() throws IOException {
        return this.stream.getFilePointer();
    }

    public void seek(long pos) throws IOException {
        this.stream.seek(pos);
    }
}

