/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.utils.UnsyncByteArrayOutputStream;

public class FullyBufferedOutputStream
extends FilterOutputStream {
    private UnsyncByteArrayOutputStream buf = new UnsyncByteArrayOutputStream();

    public FullyBufferedOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void write(int b) throws IOException {
        this.buf.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.buf.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.buf.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        this.buf.writeTo(this.out);
        this.out.close();
        this.buf.close();
    }

    @Override
    public void flush() throws IOException {
    }
}

