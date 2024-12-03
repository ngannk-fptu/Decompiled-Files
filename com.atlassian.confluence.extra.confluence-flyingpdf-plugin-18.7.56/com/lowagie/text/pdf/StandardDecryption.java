/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.crypto.AESCipher;
import com.lowagie.text.pdf.crypto.ARCFOUREncryption;

public class StandardDecryption {
    protected ARCFOUREncryption arcfour;
    protected AESCipher cipher;
    private byte[] key;
    private static final int AES_128 = 4;
    private boolean aes;
    private boolean initiated;
    private byte[] iv = new byte[16];
    private int ivptr;

    public StandardDecryption(byte[] key, int off, int len, int revision) {
        boolean bl = this.aes = revision == 4;
        if (this.aes) {
            this.key = new byte[len];
            System.arraycopy(key, off, this.key, 0, len);
        } else {
            this.arcfour = new ARCFOUREncryption();
            this.arcfour.prepareARCFOURKey(key, off, len);
        }
    }

    public byte[] update(byte[] b, int off, int len) {
        if (this.aes) {
            if (this.initiated) {
                return this.cipher.update(b, off, len);
            }
            int left = Math.min(this.iv.length - this.ivptr, len);
            System.arraycopy(b, off, this.iv, this.ivptr, left);
            off += left;
            len -= left;
            this.ivptr += left;
            if (this.ivptr == this.iv.length) {
                this.cipher = new AESCipher(false, this.key, this.iv);
                this.initiated = true;
                if (len > 0) {
                    return this.cipher.update(b, off, len);
                }
            }
            return null;
        }
        byte[] b2 = new byte[len];
        this.arcfour.encryptARCFOUR(b, off, len, b2, 0);
        return b2;
    }

    public byte[] finish() {
        if (this.aes) {
            return this.cipher.doFinal();
        }
        return null;
    }
}

