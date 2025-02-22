/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.io;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.Mac;

public class MacOutputStream
extends OutputStream {
    protected Mac mac;

    public MacOutputStream(Mac mac) {
        this.mac = mac;
    }

    @Override
    public void write(int b) throws IOException {
        this.mac.update((byte)b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.mac.update(b, off, len);
    }

    public byte[] getMac() {
        byte[] res = new byte[this.mac.getMacSize()];
        this.mac.doFinal(res, 0);
        return res;
    }
}

