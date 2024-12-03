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

public class G3413OFBBlockCipher
extends StreamBlockCipher {
    private int m;
    private int blockSize;
    private byte[] R;
    private byte[] R_init;
    private byte[] Y;
    private BlockCipher cipher;
    private int byteCount;
    private boolean initialized = false;

    public G3413OFBBlockCipher(BlockCipher cipher) {
        super(cipher);
        this.blockSize = cipher.getBlockSize();
        this.cipher = cipher;
        this.Y = new byte[this.blockSize];
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
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
        return this.cipher.getAlgorithmName() + "/OFB";
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
    protected byte calculateByte(byte in) {
        if (this.byteCount == 0) {
            this.generateY();
        }
        byte rv = (byte)(this.Y[this.byteCount] ^ in);
        ++this.byteCount;
        if (this.byteCount == this.getBlockSize()) {
            this.byteCount = 0;
            this.generateR();
        }
        return rv;
    }

    private void generateY() {
        byte[] msb = GOST3413CipherUtil.MSB(this.R, this.blockSize);
        this.cipher.processBlock(msb, 0, this.Y, 0);
    }

    private void generateR() {
        byte[] buf = GOST3413CipherUtil.LSB(this.R, this.m - this.blockSize);
        System.arraycopy(buf, 0, this.R, 0, buf.length);
        System.arraycopy(this.Y, 0, this.R, buf.length, this.m - buf.length);
    }

    @Override
    public void reset() {
        if (this.initialized) {
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            Arrays.clear(this.Y);
            this.byteCount = 0;
            this.cipher.reset();
        }
    }
}

