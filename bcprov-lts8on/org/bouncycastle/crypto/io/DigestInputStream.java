/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.Digest;

public class DigestInputStream
extends FilterInputStream {
    protected Digest digest;

    public DigestInputStream(InputStream stream, Digest digest) {
        super(stream);
        this.digest = digest;
    }

    @Override
    public int read() throws IOException {
        int b = this.in.read();
        if (b >= 0) {
            this.digest.update((byte)b);
        }
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int n = this.in.read(b, off, len);
        if (n > 0) {
            this.digest.update(b, off, n);
        }
        return n;
    }

    public Digest getDigest() {
        return this.digest;
    }
}

