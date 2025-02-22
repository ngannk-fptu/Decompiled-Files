/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.io;

import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.Mac;

public final class MacOutputStream
extends OutputStream {
    private Mac mac;

    public MacOutputStream(Mac mac) {
        this.mac = mac;
    }

    @Override
    public void write(int b) throws IOException {
        this.mac.update((byte)b);
    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        this.mac.update(bytes, off, len);
    }

    public byte[] getMac() {
        return this.mac.doFinal();
    }
}

