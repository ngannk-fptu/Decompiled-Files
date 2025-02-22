/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;

public class OldCTSBlockCipher
extends DefaultBufferedBlockCipher {
    private int blockSize;

    public OldCTSBlockCipher(BlockCipher cipher) {
        if (cipher instanceof OFBBlockCipher || cipher instanceof CFBBlockCipher) {
            throw new IllegalArgumentException("CTSBlockCipher can only accept ECB, or CBC ciphers");
        }
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        this.buf = new byte[this.blockSize * 2];
        this.bufOff = 0;
    }

    @Override
    public int getUpdateOutputSize(int len) {
        int total = len + this.bufOff;
        int leftOver = total % this.buf.length;
        if (leftOver == 0) {
            return total - this.buf.length;
        }
        return total - leftOver;
    }

    @Override
    public int getOutputSize(int len) {
        return len + this.bufOff;
    }

    @Override
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        int resultLen = 0;
        if (this.bufOff == this.buf.length) {
            resultLen = this.cipher.processBlock(this.buf, 0, out, outOff);
            System.arraycopy(this.buf, this.blockSize, this.buf, 0, this.blockSize);
            this.bufOff = this.blockSize;
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
            System.arraycopy(this.buf, blockSize, this.buf, 0, blockSize);
            this.bufOff = blockSize;
            len -= gapLen;
            inOff += gapLen;
            while (len > blockSize) {
                System.arraycopy(in, inOff, this.buf, this.bufOff, blockSize);
                resultLen += this.cipher.processBlock(this.buf, 0, out, outOff + resultLen);
                System.arraycopy(this.buf, blockSize, this.buf, 0, blockSize);
                len -= blockSize;
                inOff += blockSize;
            }
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
        return resultLen;
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
        if (this.bufOff + outOff > out.length) {
            throw new OutputLengthException("output buffer to small in doFinal");
        }
        int blockSize = this.cipher.getBlockSize();
        int len = this.bufOff - blockSize;
        byte[] block = new byte[blockSize];
        if (this.forEncryption) {
            int i;
            this.cipher.processBlock(this.buf, 0, block, 0);
            if (this.bufOff < blockSize) {
                throw new DataLengthException("need at least one block of input for CTS");
            }
            for (i = this.bufOff; i != this.buf.length; ++i) {
                this.buf[i] = block[i - blockSize];
            }
            for (i = blockSize; i != this.bufOff; ++i) {
                int n = i;
                this.buf[n] = (byte)(this.buf[n] ^ block[i - blockSize]);
            }
            if (this.cipher instanceof CBCBlockCipher) {
                BlockCipher c = ((CBCBlockCipher)this.cipher).getUnderlyingCipher();
                c.processBlock(this.buf, blockSize, out, outOff);
            } else {
                this.cipher.processBlock(this.buf, blockSize, out, outOff);
            }
            System.arraycopy(block, 0, out, outOff + blockSize, len);
        } else {
            byte[] lastBlock = new byte[blockSize];
            if (this.cipher instanceof CBCBlockCipher) {
                BlockCipher c = ((CBCBlockCipher)this.cipher).getUnderlyingCipher();
                c.processBlock(this.buf, 0, block, 0);
            } else {
                this.cipher.processBlock(this.buf, 0, block, 0);
            }
            for (int i = blockSize; i != this.bufOff; ++i) {
                lastBlock[i - blockSize] = (byte)(block[i - blockSize] ^ this.buf[i]);
            }
            System.arraycopy(this.buf, blockSize, block, 0, len);
            this.cipher.processBlock(block, 0, out, outOff);
            System.arraycopy(lastBlock, 0, out, outOff + blockSize, len);
        }
        int offset = this.bufOff;
        this.reset();
        return offset;
    }
}

