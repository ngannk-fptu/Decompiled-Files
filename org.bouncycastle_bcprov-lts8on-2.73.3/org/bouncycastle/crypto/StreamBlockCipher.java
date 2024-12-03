/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultMultiBlockCipher;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;

public abstract class StreamBlockCipher
extends DefaultMultiBlockCipher
implements StreamCipher {
    private final BlockCipher cipher;

    protected StreamBlockCipher(BlockCipher cipher) {
        this.cipher = cipher;
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override
    public final byte returnByte(byte in) {
        return this.calculateByte(in);
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        if (inOff + len > in.length) {
            throw new DataLengthException("input buffer too small");
        }
        if (outOff + len > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        int inStart = inOff;
        int inEnd = inOff + len;
        int outStart = outOff;
        while (inStart < inEnd) {
            out[outStart++] = this.calculateByte(in[inStart++]);
        }
        return len;
    }

    protected abstract byte calculateByte(byte var1);
}

