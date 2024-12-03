/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class GOFBBlockCipher
extends StreamBlockCipher {
    private byte[] IV;
    private byte[] ofbV;
    private byte[] ofbOutV;
    private int byteCount;
    private final int blockSize;
    private final BlockCipher cipher;
    boolean firstStep = true;
    int N3;
    int N4;
    static final int C1 = 0x1010104;
    static final int C2 = 0x1010101;

    public GOFBBlockCipher(BlockCipher cipher) {
        super(cipher);
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        if (this.blockSize != 8) {
            throw new IllegalArgumentException("GCTR only for 64 bit block ciphers");
        }
        this.IV = new byte[cipher.getBlockSize()];
        this.ofbV = new byte[cipher.getBlockSize()];
        this.ofbOutV = new byte[cipher.getBlockSize()];
    }

    @Override
    public void init(boolean encrypting, CipherParameters params) throws IllegalArgumentException {
        this.firstStep = true;
        this.N3 = 0;
        this.N4 = 0;
        if (params instanceof ParametersWithIV) {
            ParametersWithIV ivParam = (ParametersWithIV)params;
            byte[] iv = ivParam.getIV();
            if (iv.length < this.IV.length) {
                System.arraycopy(iv, 0, this.IV, this.IV.length - iv.length, iv.length);
                for (int i = 0; i < this.IV.length - iv.length; ++i) {
                    this.IV[i] = 0;
                }
            } else {
                System.arraycopy(iv, 0, this.IV, 0, this.IV.length);
            }
            this.reset();
            if (ivParam.getParameters() != null) {
                this.cipher.init(true, ivParam.getParameters());
            }
        } else {
            this.reset();
            if (params != null) {
                this.cipher.init(true, params);
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/GCTR";
    }

    @Override
    public int getBlockSize() {
        return this.blockSize;
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        this.processBytes(in, inOff, this.blockSize, out, outOff);
        return this.blockSize;
    }

    @Override
    public void reset() {
        this.firstStep = true;
        this.N3 = 0;
        this.N4 = 0;
        System.arraycopy(this.IV, 0, this.ofbV, 0, this.IV.length);
        this.byteCount = 0;
        this.cipher.reset();
    }

    private int bytesToint(byte[] in, int inOff) {
        return (in[inOff + 3] << 24 & 0xFF000000) + (in[inOff + 2] << 16 & 0xFF0000) + (in[inOff + 1] << 8 & 0xFF00) + (in[inOff] & 0xFF);
    }

    private void intTobytes(int num, byte[] out, int outOff) {
        out[outOff + 3] = (byte)(num >>> 24);
        out[outOff + 2] = (byte)(num >>> 16);
        out[outOff + 1] = (byte)(num >>> 8);
        out[outOff] = (byte)num;
    }

    @Override
    protected byte calculateByte(byte b) {
        if (this.byteCount == 0) {
            if (this.firstStep) {
                this.firstStep = false;
                this.cipher.processBlock(this.ofbV, 0, this.ofbOutV, 0);
                this.N3 = this.bytesToint(this.ofbOutV, 0);
                this.N4 = this.bytesToint(this.ofbOutV, 4);
            }
            this.N3 += 0x1010101;
            this.N4 += 0x1010104;
            if (this.N4 < 0x1010104 && this.N4 > 0) {
                ++this.N4;
            }
            this.intTobytes(this.N3, this.ofbV, 0);
            this.intTobytes(this.N4, this.ofbV, 4);
            this.cipher.processBlock(this.ofbV, 0, this.ofbOutV, 0);
        }
        byte rv = (byte)(this.ofbOutV[this.byteCount++] ^ b);
        if (this.byteCount == this.blockSize) {
            this.byteCount = 0;
            System.arraycopy(this.ofbV, this.blockSize, this.ofbV, 0, this.ofbV.length - this.blockSize);
            System.arraycopy(this.ofbOutV, 0, this.ofbV, this.ofbV.length - this.blockSize, this.blockSize);
        }
        return rv;
    }
}

