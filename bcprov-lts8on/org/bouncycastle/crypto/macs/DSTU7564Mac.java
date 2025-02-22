/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.digests.DSTU7564Digest;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class DSTU7564Mac
implements Mac {
    private static final int BITS_IN_BYTE = 8;
    private DSTU7564Digest engine;
    private int macSize;
    private byte[] paddedKey;
    private byte[] invertedKey;
    private long inputLength;

    public DSTU7564Mac(int macBitSize) {
        this.engine = new DSTU7564Digest(macBitSize);
        this.macSize = macBitSize / 8;
        this.paddedKey = null;
        this.invertedKey = null;
    }

    @Override
    public void init(CipherParameters params) throws IllegalArgumentException {
        this.paddedKey = null;
        this.reset();
        if (params instanceof KeyParameter) {
            byte[] key = ((KeyParameter)params).getKey();
            this.invertedKey = new byte[key.length];
            this.paddedKey = this.padKey(key);
            for (int byteIndex = 0; byteIndex < this.invertedKey.length; ++byteIndex) {
                this.invertedKey[byteIndex] = ~key[byteIndex];
            }
        } else {
            throw new IllegalArgumentException("Bad parameter passed");
        }
        this.engine.update(this.paddedKey, 0, this.paddedKey.length);
    }

    @Override
    public String getAlgorithmName() {
        return "DSTU7564Mac";
    }

    @Override
    public int getMacSize() {
        return this.macSize;
    }

    @Override
    public void update(byte in) throws IllegalStateException {
        this.engine.update(in);
        ++this.inputLength;
    }

    @Override
    public void update(byte[] in, int inOff, int len) throws DataLengthException, IllegalStateException {
        if (in.length - inOff < len) {
            throw new DataLengthException("Input buffer too short");
        }
        if (this.paddedKey == null) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        this.engine.update(in, inOff, len);
        this.inputLength += (long)len;
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.paddedKey == null) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (out.length - outOff < this.macSize) {
            throw new OutputLengthException("Output buffer too short");
        }
        this.pad();
        this.engine.update(this.invertedKey, 0, this.invertedKey.length);
        this.inputLength = 0L;
        int res = this.engine.doFinal(out, outOff);
        this.reset();
        return res;
    }

    @Override
    public void reset() {
        this.inputLength = 0L;
        this.engine.reset();
        if (this.paddedKey != null) {
            this.engine.update(this.paddedKey, 0, this.paddedKey.length);
        }
    }

    private void pad() {
        int extra = this.engine.getByteLength() - (int)(this.inputLength % (long)this.engine.getByteLength());
        if (extra < 13) {
            extra += this.engine.getByteLength();
        }
        byte[] padded = new byte[extra];
        padded[0] = -128;
        Pack.longToLittleEndian(this.inputLength * 8L, padded, padded.length - 12);
        this.engine.update(padded, 0, padded.length);
    }

    private byte[] padKey(byte[] in) {
        int paddedLen = (in.length + this.engine.getByteLength() - 1) / this.engine.getByteLength() * this.engine.getByteLength();
        int extra = paddedLen - in.length;
        if (extra < 13) {
            paddedLen += this.engine.getByteLength();
        }
        byte[] padded = new byte[paddedLen];
        System.arraycopy(in, 0, padded, 0, in.length);
        padded[in.length] = -128;
        Pack.intToLittleEndian(in.length * 8, padded, padded.length - 12);
        return padded;
    }
}

