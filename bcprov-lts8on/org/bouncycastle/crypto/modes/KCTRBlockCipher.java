/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class KCTRBlockCipher
extends StreamBlockCipher {
    private byte[] iv;
    private byte[] ofbV;
    private byte[] ofbOutV;
    private int byteCount;
    private boolean initialised;
    private BlockCipher engine;

    public KCTRBlockCipher(BlockCipher engine) {
        super(engine);
        this.engine = engine;
        this.iv = new byte[engine.getBlockSize()];
        this.ofbV = new byte[engine.getBlockSize()];
        this.ofbOutV = new byte[engine.getBlockSize()];
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.initialised = true;
        if (!(params instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("invalid parameter passed");
        }
        ParametersWithIV ivParam = (ParametersWithIV)params;
        byte[] iv = ivParam.getIV();
        int diff = this.iv.length - iv.length;
        Arrays.fill(this.iv, (byte)0);
        System.arraycopy(iv, 0, this.iv, diff, iv.length);
        params = ivParam.getParameters();
        if (params != null) {
            this.engine.init(true, params);
        }
        this.reset();
    }

    @Override
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName() + "/KCTR";
    }

    @Override
    public int getBlockSize() {
        return this.engine.getBlockSize();
    }

    @Override
    protected byte calculateByte(byte b) {
        if (this.byteCount == 0) {
            this.incrementCounterAt(0);
            this.checkCounter();
            this.engine.processBlock(this.ofbV, 0, this.ofbOutV, 0);
            return (byte)(this.ofbOutV[this.byteCount++] ^ b);
        }
        byte rv = (byte)(this.ofbOutV[this.byteCount++] ^ b);
        if (this.byteCount == this.ofbV.length) {
            this.byteCount = 0;
        }
        return rv;
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (in.length - inOff < this.getBlockSize()) {
            throw new DataLengthException("input buffer too short");
        }
        if (out.length - outOff < this.getBlockSize()) {
            throw new OutputLengthException("output buffer too short");
        }
        this.processBytes(in, inOff, this.getBlockSize(), out, outOff);
        return this.getBlockSize();
    }

    @Override
    public void reset() {
        if (this.initialised) {
            this.engine.processBlock(this.iv, 0, this.ofbV, 0);
        }
        this.engine.reset();
        this.byteCount = 0;
    }

    private void incrementCounterAt(int pos) {
        int i = pos;
        while (i < this.ofbV.length) {
            int n = i++;
            this.ofbV[n] = (byte)(this.ofbV[n] + 1);
            if (this.ofbV[n] == 0) continue;
            break;
        }
    }

    private void checkCounter() {
    }
}

