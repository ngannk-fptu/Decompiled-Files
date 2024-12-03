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

public class XTEAEngine
implements BlockCipher {
    private static final int rounds = 32;
    private static final int block_size = 8;
    private static final int delta = -1640531527;
    private int[] _S = new int[4];
    private int[] _sum0 = new int[32];
    private int[] _sum1 = new int[32];
    private boolean _initialised = false;
    private boolean _forEncryption;

    @Override
    public String getAlgorithmName() {
        return "XTEA";
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
        int j = 0;
        int i = 0;
        while (i < 4) {
            this._S[i] = this.bytesToInt(key, j);
            ++i;
            j += 4;
        }
        j = 0;
        for (i = 0; i < 32; ++i) {
            this._sum0[i] = j + this._S[j & 3];
            this._sum1[i] = (j -= 1640531527) + this._S[j >>> 11 & 3];
        }
    }

    private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int v0 = this.bytesToInt(in, inOff);
        int v1 = this.bytesToInt(in, inOff + 4);
        for (int i = 0; i < 32; ++i) {
            v1 += ((v0 += (v1 << 4 ^ v1 >>> 5) + v1 ^ this._sum0[i]) << 4 ^ v0 >>> 5) + v0 ^ this._sum1[i];
        }
        this.unpackInt(v0, out, outOff);
        this.unpackInt(v1, out, outOff + 4);
        return 8;
    }

    private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int v0 = this.bytesToInt(in, inOff);
        int v1 = this.bytesToInt(in, inOff + 4);
        for (int i = 31; i >= 0; --i) {
            v0 -= ((v1 -= (v0 << 4 ^ v0 >>> 5) + v0 ^ this._sum1[i]) << 4 ^ v1 >>> 5) + v1 ^ this._sum0[i];
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

