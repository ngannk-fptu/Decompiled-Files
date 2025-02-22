/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TeeInputStream
extends InputStream {
    private final InputStream input;
    private final OutputStream output;

    public TeeInputStream(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public int available() throws IOException {
        return this.input.available();
    }

    @Override
    public int read(byte[] buf) throws IOException {
        return this.read(buf, 0, buf.length);
    }

    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        int i = this.input.read(buf, off, len);
        if (i > 0) {
            this.output.write(buf, off, i);
        }
        return i;
    }

    @Override
    public int read() throws IOException {
        int i = this.input.read();
        if (i >= 0) {
            this.output.write(i);
        }
        return i;
    }

    @Override
    public void close() throws IOException {
        this.input.close();
        this.output.close();
    }

    public OutputStream getOutputStream() {
        return this.output;
    }
}

