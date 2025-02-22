/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.modes.GOST3413CipherUtil;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class G3413CFBBlockCipher
extends StreamBlockCipher {
    private final int s;
    private int m;
    private int blockSize;
    private byte[] R;
    private byte[] R_init;
    private BlockCipher cipher;
    private boolean forEncryption;
    private boolean initialized = false;
    private byte[] gamma;
    private byte[] inBuf;
    private int byteCount;

    public G3413CFBBlockCipher(BlockCipher cipher) {
        this(cipher, cipher.getBlockSize() * 8);
    }

    public G3413CFBBlockCipher(BlockCipher cipher, int bitBlockSize) {
        super(cipher);
        if (bitBlockSize < 0 || bitBlockSize > cipher.getBlockSize() * 8) {
            throw new IllegalArgumentException("Parameter bitBlockSize must be in range 0 < bitBlockSize <= " + cipher.getBlockSize() * 8);
        }
        this.blockSize = cipher.getBlockSize();
        this.cipher = cipher;
        this.s = bitBlockSize / 8;
        this.inBuf = new byte[this.getBlockSize()];
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        if (params instanceof ParametersWithIV) {
            ParametersWithIV ivParam = (ParametersWithIV)params;
            byte[] iv = ivParam.getIV();
            if (iv.length < this.blockSize) {
                throw new IllegalArgumentException("Parameter m must blockSize <= m");
            }
            this.m = iv.length;
            this.initArrays();
            this.R_init = Arrays.clone(iv);
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            if (ivParam.getParameters() != null) {
                this.cipher.init(true, ivParam.getParameters());
            }
        } else {
            this.setupDefaultParams();
            this.initArrays();
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            if (params != null) {
                this.cipher.init(true, params);
            }
        }
        this.initialized = true;
    }

    private void initArrays() {
        this.R = new byte[this.m];
        this.R_init = new byte[this.m];
    }

    private void setupDefaultParams() {
        this.m = 2 * this.blockSize;
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CFB" + this.blockSize * 8;
    }

    @Override
    public int getBlockSize() {
        return this.s;
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        this.processBytes(in, inOff, this.getBlockSize(), out, outOff);
        return this.getBlockSize();
    }

    @Override
    protected byte calculateByte(byte in) {
        if (this.byteCount == 0) {
            this.gamma = this.createGamma();
        }
        byte rv = (byte)(this.gamma[this.byteCount] ^ in);
        byte by = this.inBuf[this.byteCount++] = this.forEncryption ? rv : in;
        if (this.byteCount == this.getBlockSize()) {
            this.byteCount = 0;
            this.generateR(this.inBuf);
        }
        return rv;
    }

    byte[] createGamma() {
        byte[] msb = GOST3413CipherUtil.MSB(this.R, this.blockSize);
        byte[] encryptedMsb = new byte[msb.length];
        this.cipher.processBlock(msb, 0, encryptedMsb, 0);
        return GOST3413CipherUtil.MSB(encryptedMsb, this.s);
    }

    void generateR(byte[] C) {
        byte[] buf = GOST3413CipherUtil.LSB(this.R, this.m - this.s);
        System.arraycopy(buf, 0, this.R, 0, buf.length);
        System.arraycopy(C, 0, this.R, buf.length, this.m - buf.length);
    }

    @Override
    public void reset() {
        this.byteCount = 0;
        Arrays.clear(this.inBuf);
        Arrays.clear(this.gamma);
        if (this.initialized) {
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            this.cipher.reset();
        }
    }
}

