/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.httpclient.Wire;

class WireLogOutputStream
extends FilterOutputStream {
    private OutputStream out;
    private Wire wire;

    public WireLogOutputStream(OutputStream out, Wire wire) {
        super(out);
        this.out = out;
        this.wire = wire;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.out.write(b, off, len);
        this.wire.output(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        this.out.write(b);
        this.wire.output(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.out.write(b);
        this.wire.output(b);
    }
}

