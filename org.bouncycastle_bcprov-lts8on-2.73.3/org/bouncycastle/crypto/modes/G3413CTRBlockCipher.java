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

public class G3413CTRBlockCipher
extends StreamBlockCipher {
    private final int s;
    private byte[] CTR;
    private byte[] IV;
    private byte[] buf;
    private final int blockSize;
    private final BlockCipher cipher;
    private int byteCount = 0;
    private boolean initialized;

    public G3413CTRBlockCipher(BlockCipher cipher) {
        this(cipher, cipher.getBlockSize() * 8);
    }

    public G3413CTRBlockCipher(BlockCipher cipher, int bitBlockSize) {
        super(cipher);
        if (bitBlockSize < 0 || bitBlockSize > cipher.getBlockSize() * 8) {
            throw new IllegalArgumentException("Parameter bitBlockSize must be in range 0 < bitBlockSize <= " + cipher.getBlockSize() * 8);
        }
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        this.s = bitBlockSize / 8;
        this.CTR = new byte[this.blockSize];
    }

    @Override
    public void init(boolean encrypting, CipherParameters params) throws IllegalArgumentException {
        if (params instanceof ParametersWithIV) {
            ParametersWithIV ivParam = (ParametersWithIV)params;
            this.initArrays();
            this.IV = Arrays.clone(ivParam.getIV());
            if (this.IV.length != this.blockSize / 2) {
                throw new IllegalArgumentException("Parameter IV length must be == blockSize/2");
            }
            System.arraycopy(this.IV, 0, this.CTR, 0, this.IV.length);
            for (int i = this.IV.length; i < this.blockSize; ++i) {
                this.CTR[i] = 0;
            }
            if (ivParam.getParameters() != null) {
                this.cipher.init(true, ivParam.getParameters());
            }
        } else {
            this.initArrays();
            if (params != null) {
                this.cipher.init(true, params);
            }
        }
        this.initialized = true;
    }

    private void initArrays() {
        this.IV = new byte[this.blockSize / 2];
        this.CTR = new byte[this.blockSize];
        this.buf = new byte[this.s];
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/GCTR";
    }

    @Override
    public int getBlockSize() {
        return this.s;
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        this.processBytes(in, inOff, this.s, out, outOff);
        return this.s;
    }

    @Override
    protected byte calculateByte(byte in) {
        if (this.byteCount == 0) {
            this.buf = this.generateBuf();
        }
        byte rv = (byte)(this.buf[this.byteCount] ^ in);
        ++this.byteCount;
        if (this.byteCount == this.s) {
            this.byteCount = 0;
            this.generateCRT();
        }
        return rv;
    }

    private void generateCRT() {
        int n = this.CTR.length - 1;
        this.CTR[n] = (byte)(this.CTR[n] + 1);
    }

    private byte[] generateBuf() {
        byte[] encryptedCTR = new byte[this.CTR.length];
        this.cipher.processBlock(this.CTR, 0, encryptedCTR, 0);
        return GOST3413CipherUtil.MSB(encryptedCTR, this.s);
    }

    @Override
    public void reset() {
        if (this.initialized) {
            System.arraycopy(this.IV, 0, this.CTR, 0, this.IV.length);
            for (int i = this.IV.length; i < this.blockSize; ++i) {
                this.CTR[i] = 0;
            }
            this.byteCount = 0;
            this.cipher.reset();
        }
    }
}

