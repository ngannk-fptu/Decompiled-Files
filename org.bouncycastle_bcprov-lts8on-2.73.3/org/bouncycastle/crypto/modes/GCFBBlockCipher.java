/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSBox;

public class GCFBBlockCipher
extends StreamBlockCipher {
    private static final byte[] C = new byte[]{105, 0, 114, 34, 100, -55, 4, 35, -115, 58, -37, -106, 70, -23, 42, -60, 24, -2, -84, -108, 0, -19, 7, 18, -64, -122, -36, -62, -17, 76, -87, 43};
    private final CFBBlockCipher cfbEngine;
    private KeyParameter key;
    private long counter = 0L;
    private boolean forEncryption;

    public GCFBBlockCipher(BlockCipher engine) {
        super(engine);
        this.cfbEngine = new CFBBlockCipher(engine, engine.getBlockSize() * 8);
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.counter = 0L;
        this.cfbEngine.init(forEncryption, params);
        this.forEncryption = forEncryption;
        if (params instanceof ParametersWithIV) {
            params = ((ParametersWithIV)params).getParameters();
        }
        if (params instanceof ParametersWithRandom) {
            params = ((ParametersWithRandom)params).getParameters();
        }
        if (params instanceof ParametersWithSBox) {
            params = ((ParametersWithSBox)params).getParameters();
        }
        this.key = (KeyParameter)params;
    }

    @Override
    public String getAlgorithmName() {
        String name = this.cfbEngine.getAlgorithmName();
        return name.substring(0, name.indexOf(47)) + "/G" + name.substring(name.indexOf(47) + 1);
    }

    @Override
    public int getBlockSize() {
        return this.cfbEngine.getBlockSize();
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        this.processBytes(in, inOff, this.cfbEngine.getBlockSize(), out, outOff);
        return this.cfbEngine.getBlockSize();
    }

    @Override
    protected byte calculateByte(byte b) {
        if (this.counter > 0L && this.counter % 1024L == 0L) {
            BlockCipher base = this.cfbEngine.getUnderlyingCipher();
            base.init(false, this.key);
            byte[] nextKey = new byte[32];
            base.processBlock(C, 0, nextKey, 0);
            base.processBlock(C, 8, nextKey, 8);
            base.processBlock(C, 16, nextKey, 16);
            base.processBlock(C, 24, nextKey, 24);
            this.key = new KeyParameter(nextKey);
            base.init(true, this.key);
            byte[] iv = this.cfbEngine.getCurrentIV();
            base.processBlock(iv, 0, iv, 0);
            this.cfbEngine.init(this.forEncryption, new ParametersWithIV(this.key, iv));
        }
        ++this.counter;
        return this.cfbEngine.calculateByte(b);
    }

    @Override
    public void reset() {
        this.counter = 0L;
        this.cfbEngine.reset();
    }
}

