/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.io;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.Digest;

public class DigestOutputStream
extends OutputStream {
    protected Digest digest;

    public DigestOutputStream(Digest Digest2) {
        this.digest = Digest2;
    }

    @Override
    public void write(int b) throws IOException {
        this.digest.update((byte)b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.digest.update(b, off, len);
    }

    public byte[] getDigest() {
        byte[] res = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(res, 0);
        return res;
    }
}

