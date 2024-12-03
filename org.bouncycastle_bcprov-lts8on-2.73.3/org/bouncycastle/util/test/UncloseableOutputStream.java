/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.test;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UncloseableOutputStream
extends FilterOutputStream {
    public UncloseableOutputStream(OutputStream s) {
        super(s);
    }

    @Override
    public void close() {
        throw new RuntimeException("close() called on UncloseableOutputStream");
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.out.write(b, off, len);
    }
}

