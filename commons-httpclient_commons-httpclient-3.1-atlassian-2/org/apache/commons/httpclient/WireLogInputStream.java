/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.Wire;

class WireLogInputStream
extends FilterInputStream {
    private InputStream in;
    private Wire wire;

    public WireLogInputStream(InputStream in, Wire wire) {
        super(in);
        this.in = in;
        this.wire = wire;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int l = this.in.read(b, off, len);
        if (l > 0) {
            this.wire.input(b, off, l);
        }
        return l;
    }

    @Override
    public int read() throws IOException {
        int l = this.in.read();
        if (l != -1) {
            this.wire.input(l);
        }
        return l;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int l = this.in.read(b);
        if (l > 0) {
            this.wire.input(b, 0, l);
        }
        return l;
    }
}

