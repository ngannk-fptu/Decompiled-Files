/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import org.bouncycastle.crypto.io.InvalidCipherTextIOException;

public class CipherOutputStream
extends FilterOutputStream {
    private final Cipher cipher;
    private final byte[] oneByte = new byte[1];

    public CipherOutputStream(OutputStream output, Cipher cipher) {
        super(output);
        this.cipher = cipher;
    }

    @Override
    public void write(int b) throws IOException {
        this.oneByte[0] = (byte)b;
        this.write(this.oneByte, 0, 1);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] outData = this.cipher.update(b, off, len);
        if (outData != null) {
            this.out.write(outData);
        }
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override
    public void close() throws IOException {
        IOException error;
        block7: {
            error = null;
            try {
                byte[] outData = this.cipher.doFinal();
                if (outData != null) {
                    this.out.write(outData);
                }
            }
            catch (GeneralSecurityException e) {
                error = new InvalidCipherTextIOException("Error during cipher finalisation", e);
            }
            catch (Exception e) {
                error = new IOException("Error closing stream: " + e);
            }
            try {
                this.flush();
                this.out.close();
            }
            catch (IOException e) {
                if (error != null) break block7;
                error = e;
            }
        }
        if (error != null) {
            throw error;
        }
    }
}

