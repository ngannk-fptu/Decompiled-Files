/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.paddings;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.params.ParametersWithRandom;

public class PaddedBufferedBlockCipher
extends DefaultBufferedBlockCipher {
    BlockCipherPadding padding;

    public PaddedBufferedBlockCipher(BlockCipher cipher, BlockCipherPadding padding) {
        this.cipher = cipher;
        this.padding = padding;
        this.buf = new byte[cipher.getBlockSize()];
        this.bufOff = 0;
    }

    public PaddedBufferedBlockCipher(BlockCipher cipher) {
        this(cipher, new PKCS7Padding());
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        this.reset();
        if (params instanceof ParametersWithRandom) {
            ParametersWithRandom p = (ParametersWithRandom)params;
            this.padding.init(p.getRandom());
            this.cipher.init(forEncryption, p.getParameters());
        } else {
            this.padding.init(null);
            this.cipher.init(forEncryption, params);
        }
    }

    @Override
    public int getOutputSize(int len) {
        int total = len + this.bufOff;
        int leftOver = total % this.buf.length;
        if (leftOver == 0) {
            if (this.forEncryption) {
                return total + this.buf.length;
            }
            return total;
        }
        return total - leftOver + this.buf.length;
    }

    @Override
    public int getUpdateOutputSize(int len) {
        int total = len + this.bufOff;
        int leftOver = total % this.buf.length;
        if (leftOver == 0) {
            return Math.max(0, total - this.buf.length);
        }
        return total - leftOver;
    }

    @Override
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        int resultLen = 0;
        if (this.bufOff == this.buf.length) {
            resultLen = this.cipher.processBlock(this.buf, 0, out, outOff);
            this.bufOff = 0;
        }
        this.buf[this.bufOff++] = in;
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
        return resultLen;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
        int blockSize = this.cipher.getBlockSize();
        int resultLen = 0;
        if (this.forEncryption) {
            if (this.bufOff == blockSize) {
                if (outOff + 2 * blockSize > out.length) {
                    this.reset();
                    throw new OutputLengthException("output buffer too short");
                }
                resultLen = this.cipher.processBlock(this.buf, 0, out, outOff);
                this.bufOff = 0;
            }
            this.padding.addPadding(this.buf, this.bufOff);
            resultLen += this.cipher.processBlock(this.buf, 0, out, outOff + resultLen);
            this.reset();
        } else {
            if (this.bufOff != blockSize) {
                this.reset();
                throw new DataLengthException("last block incomplete in decryption");
            }
            resultLen = this.cipher.processBlock(this.buf, 0, this.buf, 0);
            this.bufOff = 0;
            try {
                System.arraycopy(this.buf, 0, out, outOff, resultLen -= this.padding.padCount(this.buf));
            }
            finally {
                this.reset();
            }
        }
        return resultLen;
    }
}

