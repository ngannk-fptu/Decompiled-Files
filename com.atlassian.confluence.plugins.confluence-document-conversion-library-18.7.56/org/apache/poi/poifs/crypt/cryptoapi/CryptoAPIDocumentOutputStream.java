/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.BoundedInputStream
 *  org.apache.commons.io.output.ByteArrayOutputStream
 */
package org.apache.poi.poifs.crypt.cryptoapi;

import java.io.InputStream;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIEncryptor;
import org.apache.poi.util.Internal;

@Internal
class CryptoAPIDocumentOutputStream
extends ByteArrayOutputStream {
    private final Cipher cipher;
    private final CryptoAPIEncryptor encryptor;
    private final byte[] oneByte = new byte[]{0};

    public CryptoAPIDocumentOutputStream(CryptoAPIEncryptor encryptor) throws GeneralSecurityException {
        this.encryptor = encryptor;
        this.cipher = encryptor.initCipherForBlock(null, 0);
    }

    public InputStream toInputStream(long maxSize) {
        return new BoundedInputStream(this.toInputStream(), maxSize);
    }

    public void setSize(int count) {
        this.count = count;
    }

    public void setBlock(int block) throws GeneralSecurityException {
        this.encryptor.initCipherForBlock(this.cipher, block);
    }

    public synchronized void write(int b) {
        try {
            this.oneByte[0] = (byte)b;
            this.cipher.update(this.oneByte, 0, 1, this.oneByte, 0);
            super.write(this.oneByte);
        }
        catch (Exception e) {
            throw new EncryptedDocumentException(e);
        }
    }

    public synchronized void write(byte[] b, int off, int len) {
        try {
            this.cipher.update(b, off, len, b, off);
            super.write(b, off, len);
        }
        catch (Exception e) {
            throw new EncryptedDocumentException(e);
        }
    }
}

