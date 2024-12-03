/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.Cipher;

class JceAADStream
extends OutputStream {
    private static final byte[] SINGLE_BYTE = new byte[1];
    private Cipher cipher;

    JceAADStream(Cipher cipher) {
        this.cipher = cipher;
    }

    @Override
    public void write(byte[] buf, int off, int len) throws IOException {
        this.cipher.updateAAD(buf, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        JceAADStream.SINGLE_BYTE[0] = (byte)b;
        this.cipher.updateAAD(SINGLE_BYTE, 0, 1);
    }
}

