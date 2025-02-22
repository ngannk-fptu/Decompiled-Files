/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import org.bouncycastle.crypto.io.InvalidCipherTextIOException;

public class CipherInputStream
extends FilterInputStream {
    private final Cipher cipher;
    private final byte[] inputBuffer = new byte[512];
    private boolean finalized = false;
    private byte[] buf;
    private int maxBuf;
    private int bufOff;

    public CipherInputStream(InputStream input, Cipher cipher) {
        super(input);
        this.cipher = cipher;
    }

    private int nextChunk() throws IOException {
        if (this.finalized) {
            return -1;
        }
        this.bufOff = 0;
        this.maxBuf = 0;
        while (this.maxBuf == 0) {
            int read = this.in.read(this.inputBuffer);
            if (read == -1) {
                this.buf = this.finaliseCipher();
                if (this.buf == null || this.buf.length == 0) {
                    return -1;
                }
                this.maxBuf = this.buf.length;
                return this.maxBuf;
            }
            this.buf = this.cipher.update(this.inputBuffer, 0, read);
            if (this.buf == null) continue;
            this.maxBuf = this.buf.length;
        }
        return this.maxBuf;
    }

    private byte[] finaliseCipher() throws InvalidCipherTextIOException {
        try {
            if (!this.finalized) {
                this.finalized = true;
                return this.cipher.doFinal();
            }
            return null;
        }
        catch (GeneralSecurityException e) {
            throw new InvalidCipherTextIOException("Error finalising cipher", e);
        }
    }

    @Override
    public int read() throws IOException {
        if (this.bufOff >= this.maxBuf && this.nextChunk() < 0) {
            return -1;
        }
        return this.buf[this.bufOff++] & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.bufOff >= this.maxBuf && this.nextChunk() < 0) {
            return -1;
        }
        int toSupply = Math.min(len, this.available());
        System.arraycopy(this.buf, this.bufOff, b, off, toSupply);
        this.bufOff += toSupply;
        return toSupply;
    }

    @Override
    public long skip(long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        int skip = (int)Math.min(n, (long)this.available());
        this.bufOff += skip;
        return skip;
    }

    @Override
    public int available() throws IOException {
        return this.maxBuf - this.bufOff;
    }

    @Override
    public void close() throws IOException {
        try {
            this.in.close();
        }
        finally {
            if (!this.finalized) {
                this.finaliseCipher();
            }
        }
        this.bufOff = 0;
        this.maxBuf = 0;
    }

    @Override
    public void mark(int readlimit) {
    }

    @Override
    public void reset() throws IOException {
    }

    @Override
    public boolean markSupported() {
        return false;
    }
}

