/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.MacCFBBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;

public class CFBBlockCipherMac
implements Mac {
    private byte[] mac;
    private byte[] buf;
    private int bufOff;
    private MacCFBBlockCipher cipher;
    private BlockCipherPadding padding = null;
    private int macSize;

    public CFBBlockCipherMac(BlockCipher cipher) {
        this(cipher, 8, cipher.getBlockSize() * 8 / 2, null);
    }

    public CFBBlockCipherMac(BlockCipher cipher, BlockCipherPadding padding) {
        this(cipher, 8, cipher.getBlockSize() * 8 / 2, padding);
    }

    public CFBBlockCipherMac(BlockCipher cipher, int cfbBitSize, int macSizeInBits) {
        this(cipher, cfbBitSize, macSizeInBits, null);
    }

    public CFBBlockCipherMac(BlockCipher cipher, int cfbBitSize, int macSizeInBits, BlockCipherPadding padding) {
        if (macSizeInBits % 8 != 0) {
            throw new IllegalArgumentException("MAC size must be multiple of 8");
        }
        this.mac = new byte[cipher.getBlockSize()];
        this.cipher = new MacCFBBlockCipher(cipher, cfbBitSize);
        this.padding = padding;
        this.macSize = macSizeInBits / 8;
        this.buf = new byte[this.cipher.getBlockSize()];
        this.bufOff = 0;
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName();
    }

    @Override
    public void init(CipherParameters params) {
        this.reset();
        this.cipher.init(params);
    }

    @Override
    public int getMacSize() {
        return this.macSize;
    }

    @Override
    public void update(byte in) {
        if (this.bufOff == this.buf.length) {
            this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
        }
        this.buf[this.bufOff++] = in;
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        if (len < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int blockSize = this.cipher.getBlockSize();
        int resultLen = 0;
        int gapLen = blockSize - this.bufOff;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
            resultLen += this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
            len -= gapLen;
            inOff += gapLen;
            while (len > blockSize) {
                resultLen += this.cipher.processBlock(in, inOff, this.mac, 0);
                len -= blockSize;
                inOff += blockSize;
            }
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        int blockSize = this.cipher.getBlockSize();
        if (this.padding == null) {
            while (this.bufOff < blockSize) {
                this.buf[this.bufOff] = 0;
                ++this.bufOff;
            }
        } else {
            this.padding.addPadding(this.buf, this.bufOff);
        }
        this.cipher.processBlock(this.buf, 0, this.mac, 0);
        this.cipher.getMacBlock(this.mac);
        System.arraycopy(this.mac, 0, out, outOff, this.macSize);
        this.reset();
        return this.macSize;
    }

    @Override
    public void reset() {
        for (int i = 0; i < this.buf.length; ++i) {
            this.buf[i] = 0;
        }
        this.bufOff = 0;
        this.cipher.reset();
    }
}

