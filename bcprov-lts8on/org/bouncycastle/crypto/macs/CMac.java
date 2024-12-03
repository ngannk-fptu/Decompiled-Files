/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class CMac
implements Mac {
    private byte[] poly;
    private byte[] ZEROES;
    private byte[] mac;
    private byte[] buf;
    private int bufOff;
    private BlockCipher cipher;
    private int macSize;
    private byte[] Lu;
    private byte[] Lu2;

    public CMac(BlockCipher cipher) {
        this(cipher, cipher.getBlockSize() * 8);
    }

    public CMac(BlockCipher cipher, int macSizeInBits) {
        if (macSizeInBits % 8 != 0) {
            throw new IllegalArgumentException("MAC size must be multiple of 8");
        }
        if (macSizeInBits > cipher.getBlockSize() * 8) {
            throw new IllegalArgumentException("MAC size must be less or equal to " + cipher.getBlockSize() * 8);
        }
        this.cipher = CBCBlockCipher.newInstance(cipher);
        this.macSize = macSizeInBits / 8;
        this.poly = CMac.lookupPoly(cipher.getBlockSize());
        this.mac = new byte[cipher.getBlockSize()];
        this.buf = new byte[cipher.getBlockSize()];
        this.ZEROES = new byte[cipher.getBlockSize()];
        this.bufOff = 0;
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName();
    }

    private static int shiftLeft(byte[] block, byte[] output) {
        int i = block.length;
        int bit = 0;
        while (--i >= 0) {
            int b = block[i] & 0xFF;
            output[i] = (byte)(b << 1 | bit);
            bit = b >>> 7 & 1;
        }
        return bit;
    }

    private byte[] doubleLu(byte[] in) {
        byte[] ret = new byte[in.length];
        int carry = CMac.shiftLeft(in, ret);
        int mask = -carry & 0xFF;
        int n = in.length - 3;
        ret[n] = (byte)(ret[n] ^ this.poly[1] & mask);
        int n2 = in.length - 2;
        ret[n2] = (byte)(ret[n2] ^ this.poly[2] & mask);
        int n3 = in.length - 1;
        ret[n3] = (byte)(ret[n3] ^ this.poly[3] & mask);
        return ret;
    }

    private static byte[] lookupPoly(int blockSizeLength) {
        int xor;
        switch (blockSizeLength * 8) {
            case 64: {
                xor = 27;
                break;
            }
            case 128: {
                xor = 135;
                break;
            }
            case 160: {
                xor = 45;
                break;
            }
            case 192: {
                xor = 135;
                break;
            }
            case 224: {
                xor = 777;
                break;
            }
            case 256: {
                xor = 1061;
                break;
            }
            case 320: {
                xor = 27;
                break;
            }
            case 384: {
                xor = 4109;
                break;
            }
            case 448: {
                xor = 2129;
                break;
            }
            case 512: {
                xor = 293;
                break;
            }
            case 768: {
                xor = 655377;
                break;
            }
            case 1024: {
                xor = 524355;
                break;
            }
            case 2048: {
                xor = 548865;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown block size for CMAC: " + blockSizeLength * 8);
            }
        }
        return Pack.intToBigEndian(xor);
    }

    @Override
    public void init(CipherParameters params) {
        this.validate(params);
        this.cipher.init(true, params);
        byte[] L = new byte[this.ZEROES.length];
        this.cipher.processBlock(this.ZEROES, 0, L, 0);
        this.Lu = this.doubleLu(L);
        this.Lu2 = this.doubleLu(this.Lu);
        this.reset();
    }

    void validate(CipherParameters params) {
        if (params != null && !(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("CMac mode only permits key to be set.");
        }
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
        int gapLen = blockSize - this.bufOff;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
            this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
            len -= gapLen;
            inOff += gapLen;
            while (len > blockSize) {
                this.cipher.processBlock(in, inOff, this.mac, 0);
                len -= blockSize;
                inOff += blockSize;
            }
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        byte[] lu;
        int blockSize = this.cipher.getBlockSize();
        if (this.bufOff == blockSize) {
            lu = this.Lu;
        } else {
            new ISO7816d4Padding().addPadding(this.buf, this.bufOff);
            lu = this.Lu2;
        }
        for (int i = 0; i < this.mac.length; ++i) {
            int n = i;
            this.buf[n] = (byte)(this.buf[n] ^ lu[i]);
        }
        this.cipher.processBlock(this.buf, 0, this.mac, 0);
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

