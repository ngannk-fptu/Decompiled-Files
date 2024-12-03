/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import java.io.IOException;
import java.io.InputStream;

public class CloseShieldInputStream
extends InputStream {
    private final InputStream parent;

    public CloseShieldInputStream(InputStream parent) {
        this.parent = parent;
    }

    public int available() throws IOException {
        return this.parent.available();
    }

    public void close() throws IOException {
    }

    public void mark(int readlimit) {
        this.parent.mark(readlimit);
    }

    public boolean markSupported() {
        return this.parent.markSupported();
    }

    public int read() throws IOException {
        return this.parent.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return this.parent.read(b, off, len);
    }

    public int read(byte[] b) throws IOException {
        return this.parent.read(b);
    }

    public void reset() throws IOException {
        this.parent.reset();
    }

    public long skip(long n) throws IOException {
        return this.parent.skip(n);
    }
}

