/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.io;

import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.Mac;

class MacUpdatingOutputStream
extends OutputStream {
    private Mac mac;

    MacUpdatingOutputStream(Mac mac) {
        this.mac = mac;
    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        this.mac.update(bytes, off, len);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        this.mac.update(bytes);
    }

    @Override
    public void write(int b) throws IOException {
        this.mac.update((byte)b);
    }
}

