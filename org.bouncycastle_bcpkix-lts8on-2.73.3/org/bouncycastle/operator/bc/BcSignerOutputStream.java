/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.crypto.CryptoException
 *  org.bouncycastle.crypto.Signer
 */
package org.bouncycastle.operator.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;

public class BcSignerOutputStream
extends OutputStream {
    private Signer sig;

    BcSignerOutputStream(Signer sig) {
        this.sig = sig;
    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        this.sig.update(bytes, off, len);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        this.sig.update(bytes, 0, bytes.length);
    }

    @Override
    public void write(int b) throws IOException {
        this.sig.update((byte)b);
    }

    byte[] getSignature() throws CryptoException {
        return this.sig.generateSignature();
    }

    boolean verify(byte[] expected) {
        return this.sig.verifySignature(expected);
    }
}

