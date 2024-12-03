/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.MultiBlockCipher;

public abstract class DefaultMultiBlockCipher
implements MultiBlockCipher {
    protected DefaultMultiBlockCipher() {
    }

    @Override
    public int getMultiBlockSize() {
        return this.getBlockSize();
    }

    @Override
    public int processBlocks(byte[] in, int inOff, int blockCount, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        int resultLen = 0;
        int blockSize = this.getMultiBlockSize();
        for (int i = 0; i != blockCount; ++i) {
            resultLen += this.processBlock(in, inOff, out, outOff + resultLen);
            inOff += blockSize;
        }
        return resultLen;
    }
}

