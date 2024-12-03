/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Pack;

public class KXTSBlockCipher
extends DefaultBufferedBlockCipher {
    private static final long RED_POLY_128 = 135L;
    private static final long RED_POLY_256 = 1061L;
    private static final long RED_POLY_512 = 293L;
    private final int blockSize;
    private final long reductionPolynomial;
    private final long[] tw_init;
    private final long[] tw_current;
    private int counter;

    protected static long getReductionPolynomial(int blockSize) {
        switch (blockSize) {
            case 16: {
                return 135L;
            }
            case 32: {
                return 1061L;
            }
            case 64: {
                return 293L;
            }
        }
        throw new IllegalArgumentException("Only 128, 256, and 512 -bit block sizes supported");
    }

    public KXTSBlockCipher(BlockCipher cipher) {
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        this.reductionPolynomial = KXTSBlockCipher.getReductionPolynomial(this.blockSize);
        this.tw_init = new long[this.blockSize >>> 3];
        this.tw_current = new long[this.blockSize >>> 3];
        this.counter = -1;
    }

    @Override
    public int getOutputSize(int length) {
        return length;
    }

    @Override
    public int getUpdateOutputSize(int len) {
        return len;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters parameters) {
        if (!(parameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("Invalid parameters passed");
        }
        ParametersWithIV ivParam = (ParametersWithIV)parameters;
        parameters = ivParam.getParameters();
        byte[] iv = ivParam.getIV();
        if (iv.length != this.blockSize) {
            throw new IllegalArgumentException("Currently only support IVs of exactly one block");
        }
        byte[] tweak = new byte[this.blockSize];
        System.arraycopy(iv, 0, tweak, 0, this.blockSize);
        this.cipher.init(true, parameters);
        this.cipher.processBlock(tweak, 0, tweak, 0);
        this.cipher.init(forEncryption, parameters);
        Pack.littleEndianToLong(tweak, 0, this.tw_init);
        System.arraycopy(this.tw_init, 0, this.tw_current, 0, this.tw_init.length);
        this.counter = 0;
    }

    @Override
    public int processByte(byte in, byte[] out, int outOff) {
        throw new IllegalStateException("unsupported operation");
    }

    @Override
    public int processBytes(byte[] input, int inOff, int len, byte[] output, int outOff) {
        if (input.length - inOff < len) {
            throw new DataLengthException("Input buffer too short");
        }
        if (output.length - inOff < len) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (len % this.blockSize != 0) {
            throw new IllegalArgumentException("Partial blocks not supported");
        }
        for (int pos = 0; pos < len; pos += this.blockSize) {
            this.processBlocks(input, inOff + pos, output, outOff + pos);
        }
        return len;
    }

    private void processBlocks(byte[] input, int inOff, byte[] output, int outOff) {
        int i;
        if (this.counter == -1) {
            throw new IllegalStateException("Attempt to process too many blocks");
        }
        ++this.counter;
        KXTSBlockCipher.GF_double(this.reductionPolynomial, this.tw_current);
        byte[] tweak = new byte[this.blockSize];
        Pack.longToLittleEndian(this.tw_current, tweak, 0);
        byte[] buffer = new byte[this.blockSize];
        System.arraycopy(tweak, 0, buffer, 0, this.blockSize);
        for (i = 0; i < this.blockSize; ++i) {
            int n = i;
            buffer[n] = (byte)(buffer[n] ^ input[inOff + i]);
        }
        this.cipher.processBlock(buffer, 0, buffer, 0);
        for (i = 0; i < this.blockSize; ++i) {
            output[outOff + i] = (byte)(buffer[i] ^ tweak[i]);
        }
    }

    @Override
    public int doFinal(byte[] output, int outOff) {
        this.reset();
        return 0;
    }

    @Override
    public void reset() {
        this.cipher.reset();
        System.arraycopy(this.tw_init, 0, this.tw_current, 0, this.tw_init.length);
        this.counter = 0;
    }

    private static void GF_double(long redPoly, long[] z) {
        long c = 0L;
        for (int i = 0; i < z.length; ++i) {
            long zVal = z[i];
            long bit = zVal >>> 63;
            z[i] = zVal << 1 ^ c;
            c = bit;
        }
        z[0] = z[0] ^ redPoly & -c;
    }
}

