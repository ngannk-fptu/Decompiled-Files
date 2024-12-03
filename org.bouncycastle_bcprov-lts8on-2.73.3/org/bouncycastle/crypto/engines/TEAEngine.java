/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.params.KeyParameter;

public class TEAEngine
implements BlockCipher {
    private static final int rounds = 32;
    private static final int block_size = 8;
    private static final int delta = -1640531527;
    private static final int d_sum = -957401312;
    private int _a;
    private int _b;
    private int _c;
    private int _d;
    private boolean _initialised = false;
    private boolean _forEncryption;

    @Override
    public String getAlgorithmName() {
        return "TEA";
    }

    @Override
    public int getBlockSize() {
        return 8;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) {
        if (!(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to TEA init - " + params.getClass().getName());
        }
        this._forEncryption = forEncryption;
        this._initialised = true;
        KeyParameter p = (KeyParameter)params;
        this.setKey(p.getKey());
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 128, params, Utils.getPurpose(forEncryption)));
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (!this._initialised) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (inOff + 8 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 8 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        return this._forEncryption ? this.encryptBlock(in, inOff, out, outOff) : this.decryptBlock(in, inOff, out, outOff);
    }

    @Override
    public void reset() {
    }

    private void setKey(byte[] key) {
        if (key.length != 16) {
            throw new IllegalArgumentException("Key size must be 128 bits.");
        }
        this._a = this.bytesToInt(key, 0);
        this._b = this.bytesToInt(key, 4);
        this._c = this.bytesToInt(key, 8);
        this._d = this.bytesToInt(key, 12);
    }

    private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int v0 = this.bytesToInt(in, inOff);
        int v1 = this.bytesToInt(in, inOff + 4);
        int sum = 0;
        for (int i = 0; i != 32; ++i) {
            v1 += ((v0 += (v1 << 4) + this._a ^ v1 + (sum -= 1640531527) ^ (v1 >>> 5) + this._b) << 4) + this._c ^ v0 + sum ^ (v0 >>> 5) + this._d;
        }
        this.unpackInt(v0, out, outOff);
        this.unpackInt(v1, out, outOff + 4);
        return 8;
    }

    private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int v0 = this.bytesToInt(in, inOff);
        int v1 = this.bytesToInt(in, inOff + 4);
        int sum = -957401312;
        for (int i = 0; i != 32; ++i) {
            v0 -= ((v1 -= (v0 << 4) + this._c ^ v0 + sum ^ (v0 >>> 5) + this._d) << 4) + this._a ^ v1 + sum ^ (v1 >>> 5) + this._b;
            sum += 1640531527;
        }
        this.unpackInt(v0, out, outOff);
        this.unpackInt(v1, out, outOff + 4);
        return 8;
    }

    private int bytesToInt(byte[] in, int inOff) {
        return in[inOff++] << 24 | (in[inOff++] & 0xFF) << 16 | (in[inOff++] & 0xFF) << 8 | in[inOff] & 0xFF;
    }

    private void unpackInt(int v, byte[] out, int outOff) {
        out[outOff++] = (byte)(v >>> 24);
        out[outOff++] = (byte)(v >>> 16);
        out[outOff++] = (byte)(v >>> 8);
        out[outOff] = (byte)v;
    }
}

