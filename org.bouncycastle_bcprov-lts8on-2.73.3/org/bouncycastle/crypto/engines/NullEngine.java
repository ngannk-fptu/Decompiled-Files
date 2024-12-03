/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;

public class NullEngine
implements BlockCipher {
    private boolean initialised;
    protected static final int DEFAULT_BLOCK_SIZE = 1;
    private final int blockSize;

    public NullEngine() {
        this(1);
    }

    public NullEngine(int blockSize) {
        this.blockSize = blockSize;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.initialised = true;
    }

    @Override
    public String getAlgorithmName() {
        return "Null";
    }

    @Override
    public int getBlockSize() {
        return this.blockSize;
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (!this.initialised) {
            throw new IllegalStateException("Null engine not initialised");
        }
        if (inOff + this.blockSize > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + this.blockSize > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = 0; i < this.blockSize; ++i) {
            out[outOff + i] = in[inOff + i];
        }
        return this.blockSize;
    }

    @Override
    public void reset() {
    }
}

