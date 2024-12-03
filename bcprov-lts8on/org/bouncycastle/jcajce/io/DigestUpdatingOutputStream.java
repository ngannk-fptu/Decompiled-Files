/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.io;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;

class DigestUpdatingOutputStream
extends OutputStream {
    private MessageDigest digest;

    DigestUpdatingOutputStream(MessageDigest digest) {
        this.digest = digest;
    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        this.digest.update(bytes, off, len);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        this.digest.update(bytes);
    }

    @Override
    public void write(int b) throws IOException {
        this.digest.update((byte)b);
    }
}

