/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.io;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.Signer;

public class SignerOutputStream
extends OutputStream {
    protected Signer signer;

    public SignerOutputStream(Signer Signer2) {
        this.signer = Signer2;
    }

    @Override
    public void write(int b) throws IOException {
        this.signer.update((byte)b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.signer.update(b, off, len);
    }

    public Signer getSigner() {
        return this.signer;
    }
}

