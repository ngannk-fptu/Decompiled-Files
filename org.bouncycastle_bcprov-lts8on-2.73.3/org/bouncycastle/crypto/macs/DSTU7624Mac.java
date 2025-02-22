/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

public class DSTU7624Mac
implements Mac {
    private static final int BITS_IN_BYTE = 8;
    private byte[] buf;
    private int bufOff;
    private int macSize;
    private int blockSize;
    private DSTU7624Engine engine;
    private byte[] c;
    private byte[] cTemp;
    private byte[] kDelta;
    private boolean initCalled = false;

    public DSTU7624Mac(int blockBitLength, int q) {
        this.engine = new DSTU7624Engine(blockBitLength);
        this.blockSize = blockBitLength / 8;
        this.macSize = q / 8;
        this.c = new byte[this.blockSize];
        this.kDelta = new byte[this.blockSize];
        this.cTemp = new byte[this.blockSize];
        this.buf = new byte[this.blockSize];
    }

    @Override
    public void init(CipherParameters params) throws IllegalArgumentException {
        if (!(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("Invalid parameter passed to DSTU7624Mac");
        }
        this.engine.init(true, params);
        this.initCalled = true;
        this.reset();
    }

    @Override
    public String getAlgorithmName() {
        return "DSTU7624Mac";
    }

    @Override
    public int getMacSize() {
        return this.macSize;
    }

    @Override
    public void update(byte in) {
        if (this.bufOff == this.buf.length) {
            this.processBlock(this.buf, 0);
            this.bufOff = 0;
        }
        this.buf[this.bufOff++] = in;
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        if (len < 0) {
            throw new IllegalArgumentException("can't have a negative input length!");
        }
        int blockSize = this.engine.getBlockSize();
        int gapLen = blockSize - this.bufOff;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
            this.processBlock(this.buf, 0);
            this.bufOff = 0;
            len -= gapLen;
            inOff += gapLen;
            while (len > blockSize) {
                this.processBlock(in, inOff);
                len -= blockSize;
                inOff += blockSize;
            }
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
    }

    private void processBlock(byte[] in, int inOff) {
        this.xor(this.c, 0, in, inOff, this.cTemp);
        this.engine.processBlock(this.cTemp, 0, this.c, 0);
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.bufOff % this.buf.length != 0) {
            throw new DataLengthException("input must be a multiple of blocksize");
        }
        this.xor(this.c, 0, this.buf, 0, this.cTemp);
        this.xor(this.cTemp, 0, this.kDelta, 0, this.c);
        this.engine.processBlock(this.c, 0, this.c, 0);
        if (this.macSize + outOff > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        System.arraycopy(this.c, 0, out, outOff, this.macSize);
        this.reset();
        return this.macSize;
    }

    @Override
    public void reset() {
        Arrays.fill(this.c, (byte)0);
        Arrays.fill(this.cTemp, (byte)0);
        Arrays.fill(this.kDelta, (byte)0);
        Arrays.fill(this.buf, (byte)0);
        this.engine.reset();
        if (this.initCalled) {
            this.engine.processBlock(this.kDelta, 0, this.kDelta, 0);
        }
        this.bufOff = 0;
    }

    private void xor(byte[] x, int xOff, byte[] y, int yOff, byte[] x_xor_y) {
        if (x.length - xOff < this.blockSize || y.length - yOff < this.blockSize || x_xor_y.length < this.blockSize) {
            throw new IllegalArgumentException("some of input buffers too short");
        }
        for (int byteIndex = 0; byteIndex < this.blockSize; ++byteIndex) {
            x_xor_y[byteIndex] = (byte)(x[byteIndex + xOff] ^ y[byteIndex + yOff]);
        }
    }
}

