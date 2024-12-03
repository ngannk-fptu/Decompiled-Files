/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.paddings;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedMultiBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class PaddedBufferedMultiBlockCipher
extends DefaultBufferedMultiBlockCipher {
    BlockCipherPadding padding;

    public PaddedBufferedMultiBlockCipher(MultiBlockCipher cipher, BlockCipherPadding padding) {
        super(cipher);
        this.padding = padding;
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
            if (forEncryption) {
                this.padding.init(null);
            }
            this.cipher.init(forEncryption, params);
        }
        this.buf = new byte[this.cipher.getMultiBlockSize()];
        this.bufOff = 0;
    }

    @Override
    public int getOutputSize(int len) {
        int total = len + this.bufOff;
        int leftOver = total % this.blockSize;
        if (leftOver == 0) {
            if (this.forEncryption) {
                return total + this.blockSize;
            }
            return total;
        }
        return total - leftOver + this.blockSize;
    }

    @Override
    public int getUpdateOutputSize(int len) {
        int total = len + this.bufOff;
        int leftOver = total % this.blockSize;
        if (!this.forEncryption && leftOver == 0) {
            return Math.max(0, total - this.blockSize);
        }
        return total - leftOver;
    }

    @Override
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        int resultLen = 0;
        if (this.bufOff == this.buf.length) {
            resultLen += this.cipher.processBlocks(this.buf, 0, this.buf.length / this.blockSize, out, outOff);
            this.bufOff = 0;
        }
        this.buf[this.bufOff++] = in;
        return resultLen;
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (len < 0) {
            throw new IllegalArgumentException("input length cannot be negative");
        }
        int length = this.getUpdateOutputSize(len);
        if (length > 0 && outOff + length > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        int resultLen = 0;
        int gapLen = this.buf.length - this.bufOff;
        if (len > gapLen) {
            int blockCount;
            if (this.bufOff != 0) {
                System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
                resultLen += this.cipher.processBlocks(this.buf, 0, this.buf.length / this.blockSize, out, outOff);
                this.bufOff = 0;
                len -= gapLen;
                inOff += gapLen;
            }
            if (len > this.buf.length && (blockCount = this.forEncryption ? len / this.blockSize : len / this.blockSize - (len % this.blockSize == 0 ? 1 : 0)) > 0) {
                resultLen += this.cipher.processBlocks(in, inOff, blockCount, out, outOff + resultLen);
                int processed = blockCount * this.blockSize;
                inOff += processed;
                if ((len -= processed) == 0) {
                    System.arraycopy(in, inOff - this.blockSize, this.buf, 0, this.blockSize);
                }
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
        int resultLen = 0;
        if (this.forEncryption) {
            byte[] pad = new byte[this.blockSize];
            if (this.bufOff == this.cipher.getMultiBlockSize()) {
                if (outOff + this.cipher.getMultiBlockSize() + this.blockSize > out.length) {
                    this.reset();
                    throw new OutputLengthException("output buffer too short");
                }
                System.arraycopy(this.buf, this.bufOff - this.blockSize, pad, 0, this.blockSize);
                resultLen = this.cipher.processBlocks(this.buf, 0, this.bufOff / this.blockSize, out, outOff);
                this.bufOff = 0;
            } else if (this.bufOff == 0) {
                System.arraycopy(this.buf, 0, pad, 0, this.blockSize);
            } else if (this.bufOff > this.blockSize) {
                System.arraycopy(this.buf, (this.bufOff / this.blockSize - 1) * this.blockSize, pad, 0, this.blockSize);
            }
            int padOff = this.bufOff % this.blockSize;
            if (padOff != 0) {
                System.arraycopy(this.buf, this.bufOff - padOff, pad, 0, padOff);
            }
            this.padding.addPadding(pad, padOff);
            if (this.bufOff == 0) {
                System.arraycopy(pad, 0, this.buf, 0, this.blockSize);
                this.bufOff += this.blockSize;
            } else {
                System.arraycopy(pad, 0, this.buf, this.bufOff / this.blockSize * this.blockSize, this.blockSize);
                this.bufOff += this.blockSize - padOff;
            }
            resultLen += this.cipher.processBlocks(this.buf, 0, this.bufOff / this.blockSize, out, outOff + resultLen);
            this.reset();
        } else {
            if (this.bufOff % this.blockSize != 0) {
                this.reset();
                throw new DataLengthException("last block incomplete in decryption");
            }
            resultLen = this.cipher.processBlocks(this.buf, 0, this.bufOff / this.blockSize, this.buf, 0);
            try {
                resultLen -= this.padding.padCount(Arrays.copyOfRange(this.buf, resultLen - this.blockSize, resultLen));
                System.arraycopy(this.buf, 0, out, outOff, resultLen);
            }
            finally {
                this.reset();
            }
        }
        return resultLen;
    }
}

