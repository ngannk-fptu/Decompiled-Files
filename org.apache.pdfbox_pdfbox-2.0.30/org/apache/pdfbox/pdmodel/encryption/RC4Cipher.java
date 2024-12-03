/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.encryption;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class RC4Cipher {
    private final int[] salt = new int[256];
    private int b;
    private int c;

    RC4Cipher() {
    }

    public void setKey(byte[] key) {
        this.b = 0;
        this.c = 0;
        if (key.length < 1 || key.length > 32) {
            throw new IllegalArgumentException("number of bytes must be between 1 and 32");
        }
        for (int i = 0; i < this.salt.length; ++i) {
            this.salt[i] = i;
        }
        int keyIndex = 0;
        int saltIndex = 0;
        for (int i = 0; i < this.salt.length; ++i) {
            saltIndex = (RC4Cipher.fixByte(key[keyIndex]) + this.salt[i] + saltIndex) % 256;
            RC4Cipher.swap(this.salt, i, saltIndex);
            keyIndex = (keyIndex + 1) % key.length;
        }
    }

    private static int fixByte(byte aByte) {
        return aByte < 0 ? 256 + aByte : aByte;
    }

    private static void swap(int[] data, int firstIndex, int secondIndex) {
        int tmp = data[firstIndex];
        data[firstIndex] = data[secondIndex];
        data[secondIndex] = tmp;
    }

    public void write(byte aByte, OutputStream output) throws IOException {
        this.b = (this.b + 1) % 256;
        this.c = (this.salt[this.b] + this.c) % 256;
        RC4Cipher.swap(this.salt, this.b, this.c);
        int saltIndex = (this.salt[this.b] + this.salt[this.c]) % 256;
        output.write(aByte ^ (byte)this.salt[saltIndex]);
    }

    public void write(byte[] data, OutputStream output) throws IOException {
        for (byte aData : data) {
            this.write(aData, output);
        }
    }

    public void write(InputStream data, OutputStream output) throws IOException {
        int amountRead;
        byte[] buffer = new byte[1024];
        while ((amountRead = data.read(buffer)) != -1) {
            this.write(buffer, 0, amountRead, output);
        }
    }

    public void write(byte[] data, int offset, int len, OutputStream output) throws IOException {
        for (int i = offset; i < offset + len; ++i) {
            this.write(data[i], output);
        }
    }
}

