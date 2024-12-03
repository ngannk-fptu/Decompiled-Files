/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.Mac;

public class MacInputStream
extends FilterInputStream {
    protected Mac mac;

    public MacInputStream(InputStream stream, Mac mac) {
        super(stream);
        this.mac = mac;
    }

    @Override
    public int read() throws IOException {
        int b = this.in.read();
        if (b >= 0) {
            this.mac.update((byte)b);
        }
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int n = this.in.read(b, off, len);
        if (n >= 0) {
            this.mac.update(b, off, n);
        }
        return n;
    }

    public Mac getMac() {
        return this.mac;
    }
}

