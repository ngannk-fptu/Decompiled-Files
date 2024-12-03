/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.OutputStream;

public class TeeOutputStream
extends OutputStream {
    private OutputStream left;
    private OutputStream right;

    public TeeOutputStream(OutputStream left, OutputStream right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void close() throws IOException {
        try {
            this.left.close();
        }
        finally {
            this.right.close();
        }
    }

    @Override
    public void flush() throws IOException {
        this.left.flush();
        this.right.flush();
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.left.write(b);
        this.right.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.left.write(b, off, len);
        this.right.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        this.left.write(b);
        this.right.write(b);
    }
}

