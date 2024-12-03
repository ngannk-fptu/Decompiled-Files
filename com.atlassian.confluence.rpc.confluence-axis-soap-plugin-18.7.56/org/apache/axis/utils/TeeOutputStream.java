/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

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

    public void close() throws IOException {
        this.left.close();
        this.right.close();
    }

    public void flush() throws IOException {
        this.left.flush();
        this.right.flush();
    }

    public void write(byte[] b) throws IOException {
        this.left.write(b);
        this.right.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        this.left.write(b, off, len);
        this.right.write(b, off, len);
    }

    public void write(int b) throws IOException {
        this.left.write(b);
        this.right.write(b);
    }
}

