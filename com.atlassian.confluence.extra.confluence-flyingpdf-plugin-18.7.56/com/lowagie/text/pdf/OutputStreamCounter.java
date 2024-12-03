/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamCounter
extends OutputStream {
    protected OutputStream out;
    protected long counter = 0L;

    public OutputStreamCounter(OutputStream out) {
        this.out = out;
    }

    @Override
    public void close() throws IOException {
        this.out.close();
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.counter += (long)b.length;
        this.out.write(b);
    }

    @Override
    public void write(int b) throws IOException {
        ++this.counter;
        this.out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.counter += (long)len;
        this.out.write(b, off, len);
    }

    public long getCounter() {
        return this.counter;
    }

    public void resetCounter() {
        this.counter = 0L;
    }
}

