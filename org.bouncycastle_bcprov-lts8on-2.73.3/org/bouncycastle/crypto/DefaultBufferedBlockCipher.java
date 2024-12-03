/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;

public class DefaultBufferedBlockCipher
implements BufferedBlockCipher {
    protected byte[] buf;
    protected int bufOff;
    protected boolean forEncryption;
    protected BlockCipher cipher;
    protected boolean partialBlockOkay;
    protected boolean pgpCFB;

    protected DefaultBufferedBlockCipher() {
    }

    public DefaultBufferedBlockCipher(BlockCipher cipher) {
        this.cipher = cipher;
        this.buf = new byte[cipher.getBlockSize()];
        this.bufOff = 0;
        String name = cipher.getAlgorithmName();
        int idx = name.indexOf(47) + 1;
        boolean bl = this.pgpCFB = idx > 0 && name.startsWith("PGP", idx);
        this.partialBlockOkay = this.pgpCFB || cipher instanceof StreamCipher ? true : idx > 0 && name.startsWith("OpenPGP", idx);
    }

    @Override
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        this.reset();
        this.cipher.init(forEncryption, params);
    }

    @Override
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    @Override
    public int getUpdateOutputSize(int len) {
        int total = len + this.bufOff;
        int leftOver = this.pgpCFB ? (this.forEncryption ? total % this.buf.length - (this.cipher.getBlockSize() + 2) : total % this.buf.length) : (this.partialBlockOkay ? 0 : total % this.buf.length);
        return total - leftOver;
    }

    @Override
    public int getOutputSize(int length) {
        if (this.pgpCFB && this.forEncryption) {
            return length + this.bufOff + (this.cipher.getBlockSize() + 2);
        }
        return length + this.bufOff;
    }

    @Override
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
        int resultLen = 0;
        this.buf[this.bufOff++] = in;
        if (this.bufOff == this.buf.length) {
            resultLen += this.cipher.processBlock(this.buf, 0, out, outOff);
            this.bufOff = 0;
        }
        return resultLen;
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (len < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int blockSize = this.getBlockSize();
        int length = this.getUpdateOutputSize(len);
        if (length > 0 && outOff + length > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        int resultLen = 0;
        int gapLen = this.buf.length - this.bufOff;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
            resultLen += this.cipher.processBlock(this.buf, 0, out, outOff);
            this.bufOff = 0;
            len -= gapLen;
            inOff += gapLen;
            while (len > this.buf.length) {
                resultLen += this.cipher.processBlock(in, inOff, out, outOff + resultLen);
                len -= blockSize;
                inOff += blockSize;
            }
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
        if (this.bufOff == this.buf.length) {
            resultLen += this.cipher.processBlock(this.buf, 0, out, outOff + resultLen);
            this.bufOff = 0;
        }
        return resultLen;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
        try {
            int resultLen = 0;
            if (outOff + this.bufOff > out.length) {
                throw new OutputLengthException("output buffer too short for doFinal()");
            }
            if (this.bufOff != 0) {
                if (!this.partialBlockOkay) {
                    throw new DataLengthException("data not block size aligned");
                }
                this.cipher.processBlock(this.buf, 0, this.buf, 0);
                resultLen = this.bufOff;
                this.bufOff = 0;
                System.arraycopy(this.buf, 0, out, outOff, resultLen);
            }
            int n = resultLen;
            return n;
        }
        finally {
            this.reset();
        }
    }

    @Override
    public void reset() {
        for (int i = 0; i < this.buf.length; ++i) {
            this.buf[i] = 0;
        }
        this.bufOff = 0;
        this.cipher.reset();
    }

    public String toString() {
        return "DefaultBufferedBlockCipher(" + this.cipher.toString() + ")";
    }
}

