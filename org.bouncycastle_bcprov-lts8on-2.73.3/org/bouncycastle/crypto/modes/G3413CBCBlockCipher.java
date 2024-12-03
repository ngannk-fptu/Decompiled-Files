/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.modes.GOST3413CipherUtil;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class G3413CBCBlockCipher
implements BlockCipher {
    private int m;
    private int blockSize;
    private byte[] R;
    private byte[] R_init;
    private BlockCipher cipher;
    private boolean initialized = false;
    private boolean forEncryption;

    public G3413CBCBlockCipher(BlockCipher cipher) {
        this.blockSize = cipher.getBlockSize();
        this.cipher = cipher;
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
                this.cipher.init(forEncryption, ivParam.getParameters());
            }
        } else {
            this.setupDefaultParams();
            this.initArrays();
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            if (params != null) {
                this.cipher.init(forEncryption, params);
            }
        }
        this.initialized = true;
    }

    private void initArrays() {
        this.R = new byte[this.m];
        this.R_init = new byte[this.m];
    }

    private void setupDefaultParams() {
        this.m = this.blockSize;
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CBC";
    }

    @Override
    public int getBlockSize() {
        return this.blockSize;
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        return this.forEncryption ? this.encrypt(in, inOff, out, outOff) : this.decrypt(in, inOff, out, outOff);
    }

    private int encrypt(byte[] in, int inOff, byte[] out, int outOff) {
        byte[] msb = GOST3413CipherUtil.MSB(this.R, this.blockSize);
        byte[] input = GOST3413CipherUtil.copyFromInput(in, this.blockSize, inOff);
        byte[] sum = GOST3413CipherUtil.sum(input, msb);
        byte[] c = new byte[sum.length];
        this.cipher.processBlock(sum, 0, c, 0);
        System.arraycopy(c, 0, out, outOff, c.length);
        if (out.length > outOff + sum.length) {
            this.generateR(c);
        }
        return c.length;
    }

    private int decrypt(byte[] in, int inOff, byte[] out, int outOff) {
        byte[] msb = GOST3413CipherUtil.MSB(this.R, this.blockSize);
        byte[] input = GOST3413CipherUtil.copyFromInput(in, this.blockSize, inOff);
        byte[] c = new byte[input.length];
        this.cipher.processBlock(input, 0, c, 0);
        byte[] sum = GOST3413CipherUtil.sum(c, msb);
        System.arraycopy(sum, 0, out, outOff, sum.length);
        if (out.length > outOff + sum.length) {
            this.generateR(input);
        }
        return sum.length;
    }

    private void generateR(byte[] C) {
        byte[] buf = GOST3413CipherUtil.LSB(this.R, this.m - this.blockSize);
        System.arraycopy(buf, 0, this.R, 0, buf.length);
        System.arraycopy(C, 0, this.R, buf.length, this.m - buf.length);
    }

    @Override
    public void reset() {
        if (this.initialized) {
            System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
            this.cipher.reset();
        }
    }
}

