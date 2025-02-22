/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Pack;

public class Poly1305
implements Mac {
    private static final int BLOCK_SIZE = 16;
    private final BlockCipher cipher;
    private final byte[] singleByte = new byte[1];
    private int r0;
    private int r1;
    private int r2;
    private int r3;
    private int r4;
    private int s1;
    private int s2;
    private int s3;
    private int s4;
    private int k0;
    private int k1;
    private int k2;
    private int k3;
    private final byte[] currentBlock = new byte[16];
    private int currentBlockOffset = 0;
    private int h0;
    private int h1;
    private int h2;
    private int h3;
    private int h4;

    public Poly1305() {
        this.cipher = null;
    }

    public Poly1305(BlockCipher cipher) {
        if (cipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("Poly1305 requires a 128 bit block cipher.");
        }
        this.cipher = cipher;
    }

    @Override
    public void init(CipherParameters params) throws IllegalArgumentException {
        byte[] nonce = null;
        if (this.cipher != null) {
            if (!(params instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("Poly1305 requires an IV when used with a block cipher.");
            }
            ParametersWithIV ivParams = (ParametersWithIV)params;
            nonce = ivParams.getIV();
            params = ivParams.getParameters();
        }
        if (!(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("Poly1305 requires a key.");
        }
        KeyParameter keyParams = (KeyParameter)params;
        this.setKey(keyParams.getKey(), nonce);
        this.reset();
    }

    private void setKey(byte[] key, byte[] nonce) {
        int kOff;
        byte[] kBytes;
        if (key.length != 32) {
            throw new IllegalArgumentException("Poly1305 key must be 256 bits.");
        }
        if (this.cipher != null && (nonce == null || nonce.length != 16)) {
            throw new IllegalArgumentException("Poly1305 requires a 128 bit IV.");
        }
        int t0 = Pack.littleEndianToInt(key, 0);
        int t1 = Pack.littleEndianToInt(key, 4);
        int t2 = Pack.littleEndianToInt(key, 8);
        int t3 = Pack.littleEndianToInt(key, 12);
        this.r0 = t0 & 0x3FFFFFF;
        this.r1 = (t0 >>> 26 | t1 << 6) & 0x3FFFF03;
        this.r2 = (t1 >>> 20 | t2 << 12) & 0x3FFC0FF;
        this.r3 = (t2 >>> 14 | t3 << 18) & 0x3F03FFF;
        this.r4 = t3 >>> 8 & 0xFFFFF;
        this.s1 = this.r1 * 5;
        this.s2 = this.r2 * 5;
        this.s3 = this.r3 * 5;
        this.s4 = this.r4 * 5;
        if (this.cipher == null) {
            kBytes = key;
            kOff = 16;
        } else {
            kBytes = new byte[16];
            kOff = 0;
            this.cipher.init(true, new KeyParameter(key, 16, 16));
            this.cipher.processBlock(nonce, 0, kBytes, 0);
        }
        this.k0 = Pack.littleEndianToInt(kBytes, kOff + 0);
        this.k1 = Pack.littleEndianToInt(kBytes, kOff + 4);
        this.k2 = Pack.littleEndianToInt(kBytes, kOff + 8);
        this.k3 = Pack.littleEndianToInt(kBytes, kOff + 12);
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher == null ? "Poly1305" : "Poly1305-" + this.cipher.getAlgorithmName();
    }

    @Override
    public int getMacSize() {
        return 16;
    }

    @Override
    public void update(byte in) throws IllegalStateException {
        this.singleByte[0] = in;
        this.update(this.singleByte, 0, 1);
    }

    @Override
    public void update(byte[] in, int inOff, int len) throws DataLengthException, IllegalStateException {
        int copied = 0;
        while (len > copied) {
            if (this.currentBlockOffset == 16) {
                this.processBlock();
                this.currentBlockOffset = 0;
            }
            int toCopy = Math.min(len - copied, 16 - this.currentBlockOffset);
            System.arraycopy(in, copied + inOff, this.currentBlock, this.currentBlockOffset, toCopy);
            copied += toCopy;
            this.currentBlockOffset += toCopy;
        }
    }

    private void processBlock() {
        if (this.currentBlockOffset < 16) {
            this.currentBlock[this.currentBlockOffset] = 1;
            for (int i = this.currentBlockOffset + 1; i < 16; ++i) {
                this.currentBlock[i] = 0;
            }
        }
        long t0 = 0xFFFFFFFFL & (long)Pack.littleEndianToInt(this.currentBlock, 0);
        long t1 = 0xFFFFFFFFL & (long)Pack.littleEndianToInt(this.currentBlock, 4);
        long t2 = 0xFFFFFFFFL & (long)Pack.littleEndianToInt(this.currentBlock, 8);
        long t3 = 0xFFFFFFFFL & (long)Pack.littleEndianToInt(this.currentBlock, 12);
        this.h0 = (int)((long)this.h0 + (t0 & 0x3FFFFFFL));
        this.h1 = (int)((long)this.h1 + ((t1 << 32 | t0) >>> 26 & 0x3FFFFFFL));
        this.h2 = (int)((long)this.h2 + ((t2 << 32 | t1) >>> 20 & 0x3FFFFFFL));
        this.h3 = (int)((long)this.h3 + ((t3 << 32 | t2) >>> 14 & 0x3FFFFFFL));
        this.h4 = (int)((long)this.h4 + (t3 >>> 8));
        if (this.currentBlockOffset == 16) {
            this.h4 += 0x1000000;
        }
        long tp0 = Poly1305.mul32x32_64(this.h0, this.r0) + Poly1305.mul32x32_64(this.h1, this.s4) + Poly1305.mul32x32_64(this.h2, this.s3) + Poly1305.mul32x32_64(this.h3, this.s2) + Poly1305.mul32x32_64(this.h4, this.s1);
        long tp1 = Poly1305.mul32x32_64(this.h0, this.r1) + Poly1305.mul32x32_64(this.h1, this.r0) + Poly1305.mul32x32_64(this.h2, this.s4) + Poly1305.mul32x32_64(this.h3, this.s3) + Poly1305.mul32x32_64(this.h4, this.s2);
        long tp2 = Poly1305.mul32x32_64(this.h0, this.r2) + Poly1305.mul32x32_64(this.h1, this.r1) + Poly1305.mul32x32_64(this.h2, this.r0) + Poly1305.mul32x32_64(this.h3, this.s4) + Poly1305.mul32x32_64(this.h4, this.s3);
        long tp3 = Poly1305.mul32x32_64(this.h0, this.r3) + Poly1305.mul32x32_64(this.h1, this.r2) + Poly1305.mul32x32_64(this.h2, this.r1) + Poly1305.mul32x32_64(this.h3, this.r0) + Poly1305.mul32x32_64(this.h4, this.s4);
        long tp4 = Poly1305.mul32x32_64(this.h0, this.r4) + Poly1305.mul32x32_64(this.h1, this.r3) + Poly1305.mul32x32_64(this.h2, this.r2) + Poly1305.mul32x32_64(this.h3, this.r1) + Poly1305.mul32x32_64(this.h4, this.r0);
        this.h0 = (int)tp0 & 0x3FFFFFF;
        this.h1 = (int)(tp1 += tp0 >>> 26) & 0x3FFFFFF;
        this.h2 = (int)(tp2 += tp1 >>> 26) & 0x3FFFFFF;
        this.h3 = (int)(tp3 += tp2 >>> 26) & 0x3FFFFFF;
        this.h4 = (int)(tp4 += tp3 >>> 26) & 0x3FFFFFF;
        this.h0 += (int)(tp4 >>> 26) * 5;
        this.h1 += this.h0 >>> 26;
        this.h0 &= 0x3FFFFFF;
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (outOff + 16 > out.length) {
            throw new OutputLengthException("Output buffer is too short.");
        }
        if (this.currentBlockOffset > 0) {
            this.processBlock();
        }
        this.h1 += this.h0 >>> 26;
        this.h0 &= 0x3FFFFFF;
        this.h2 += this.h1 >>> 26;
        this.h1 &= 0x3FFFFFF;
        this.h3 += this.h2 >>> 26;
        this.h2 &= 0x3FFFFFF;
        this.h4 += this.h3 >>> 26;
        this.h3 &= 0x3FFFFFF;
        this.h0 += (this.h4 >>> 26) * 5;
        this.h4 &= 0x3FFFFFF;
        this.h1 += this.h0 >>> 26;
        this.h0 &= 0x3FFFFFF;
        int g0 = this.h0 + 5;
        int b = g0 >>> 26;
        g0 &= 0x3FFFFFF;
        int g1 = this.h1 + b;
        b = g1 >>> 26;
        g1 &= 0x3FFFFFF;
        int g2 = this.h2 + b;
        b = g2 >>> 26;
        g2 &= 0x3FFFFFF;
        int g3 = this.h3 + b;
        b = g3 >>> 26;
        g3 &= 0x3FFFFFF;
        int g4 = this.h4 + b - 0x4000000;
        b = (g4 >>> 31) - 1;
        int nb = ~b;
        this.h0 = this.h0 & nb | g0 & b;
        this.h1 = this.h1 & nb | g1 & b;
        this.h2 = this.h2 & nb | g2 & b;
        this.h3 = this.h3 & nb | g3 & b;
        this.h4 = this.h4 & nb | g4 & b;
        long f0 = ((long)(this.h0 | this.h1 << 26) & 0xFFFFFFFFL) + (0xFFFFFFFFL & (long)this.k0);
        long f1 = ((long)(this.h1 >>> 6 | this.h2 << 20) & 0xFFFFFFFFL) + (0xFFFFFFFFL & (long)this.k1);
        long f2 = ((long)(this.h2 >>> 12 | this.h3 << 14) & 0xFFFFFFFFL) + (0xFFFFFFFFL & (long)this.k2);
        long f3 = ((long)(this.h3 >>> 18 | this.h4 << 8) & 0xFFFFFFFFL) + (0xFFFFFFFFL & (long)this.k3);
        Pack.intToLittleEndian((int)f0, out, outOff);
        Pack.intToLittleEndian((int)(f1 += f0 >>> 32), out, outOff + 4);
        Pack.intToLittleEndian((int)(f2 += f1 >>> 32), out, outOff + 8);
        Pack.intToLittleEndian((int)(f3 += f2 >>> 32), out, outOff + 12);
        this.reset();
        return 16;
    }

    @Override
    public void reset() {
        this.currentBlockOffset = 0;
        this.h4 = 0;
        this.h3 = 0;
        this.h2 = 0;
        this.h1 = 0;
        this.h0 = 0;
    }

    private static final long mul32x32_64(int i1, int i2) {
        return ((long)i1 & 0xFFFFFFFFL) * (long)i2;
    }
}

