/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

public class CloseShieldReader
extends Reader {
    private final Reader parent;

    public CloseShieldReader(Reader parent) {
        this.parent = parent;
    }

    public void close() throws IOException {
    }

    public void mark(int readAheadLimit) throws IOException {
        this.parent.mark(readAheadLimit);
    }

    public boolean markSupported() {
        return this.parent.markSupported();
    }

    public int read() throws IOException {
        return this.parent.read();
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        return this.parent.read(cbuf, off, len);
    }

    public int read(char[] cbuf) throws IOException {
        return this.parent.read(cbuf);
    }

    public int read(CharBuffer target) throws IOException {
        return this.parent.read(target);
    }

    public boolean ready() throws IOException {
        return this.parent.ready();
    }

    public void reset() throws IOException {
        this.parent.reset();
    }

    public long skip(long n) throws IOException {
        return this.parent.skip(n);
    }
}

