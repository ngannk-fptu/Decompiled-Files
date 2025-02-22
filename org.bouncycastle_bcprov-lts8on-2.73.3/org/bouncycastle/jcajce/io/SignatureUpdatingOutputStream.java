/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.io;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Signature;
import java.security.SignatureException;
import org.bouncycastle.util.Exceptions;

class SignatureUpdatingOutputStream
extends OutputStream {
    private Signature sig;

    SignatureUpdatingOutputStream(Signature sig) {
        this.sig = sig;
    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        try {
            this.sig.update(bytes, off, len);
        }
        catch (SignatureException e) {
            throw Exceptions.ioException(e.getMessage(), e);
        }
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        try {
            this.sig.update(bytes);
        }
        catch (SignatureException e) {
            throw Exceptions.ioException(e.getMessage(), e);
        }
    }

    @Override
    public void write(int b) throws IOException {
        try {
            this.sig.update((byte)b);
        }
        catch (SignatureException e) {
            throw Exceptions.ioException(e.getMessage(), e);
        }
    }
}

