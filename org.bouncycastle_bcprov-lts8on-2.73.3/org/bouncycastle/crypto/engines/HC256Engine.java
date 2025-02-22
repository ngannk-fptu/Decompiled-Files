/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class HC256Engine
implements StreamCipher {
    private int[] p = new int[1024];
    private int[] q = new int[1024];
    private int cnt = 0;
    private byte[] key;
    private byte[] iv;
    private boolean initialised;
    private byte[] buf = new byte[4];
    private int idx = 0;

    private int step() {
        int ret;
        int j = this.cnt & 0x3FF;
        if (this.cnt < 1024) {
            int x = this.p[j - 3 & 0x3FF];
            int y = this.p[j - 1023 & 0x3FF];
            int n = j;
            this.p[n] = this.p[n] + (this.p[j - 10 & 0x3FF] + (HC256Engine.rotateRight(x, 10) ^ HC256Engine.rotateRight(y, 23)) + this.q[(x ^ y) & 0x3FF]);
            x = this.p[j - 12 & 0x3FF];
            ret = this.q[x & 0xFF] + this.q[(x >> 8 & 0xFF) + 256] + this.q[(x >> 16 & 0xFF) + 512] + this.q[(x >> 24 & 0xFF) + 768] ^ this.p[j];
        } else {
            int x = this.q[j - 3 & 0x3FF];
            int y = this.q[j - 1023 & 0x3FF];
            int n = j;
            this.q[n] = this.q[n] + (this.q[j - 10 & 0x3FF] + (HC256Engine.rotateRight(x, 10) ^ HC256Engine.rotateRight(y, 23)) + this.p[(x ^ y) & 0x3FF]);
            x = this.q[j - 12 & 0x3FF];
            ret = this.p[x & 0xFF] + this.p[(x >> 8 & 0xFF) + 256] + this.p[(x >> 16 & 0xFF) + 512] + this.p[(x >> 24 & 0xFF) + 768] ^ this.q[j];
        }
        this.cnt = this.cnt + 1 & 0x7FF;
        return ret;
    }

    private void init() {
        int i;
        if (this.key.length != 32 && this.key.length != 16) {
            throw new IllegalArgumentException("The key must be 128/256 bits long");
        }
        if (this.iv.length < 16) {
            throw new IllegalArgumentException("The IV must be at least 128 bits long");
        }
        if (this.key.length != 32) {
            byte[] k = new byte[32];
            System.arraycopy(this.key, 0, k, 0, this.key.length);
            System.arraycopy(this.key, 0, k, 16, this.key.length);
            this.key = k;
        }
        if (this.iv.length < 32) {
            byte[] newIV = new byte[32];
            System.arraycopy(this.iv, 0, newIV, 0, this.iv.length);
            System.arraycopy(this.iv, 0, newIV, this.iv.length, newIV.length - this.iv.length);
            this.iv = newIV;
        }
        this.idx = 0;
        this.cnt = 0;
        int[] w = new int[2560];
        for (i = 0; i < 32; ++i) {
            int n = i >> 2;
            w[n] = w[n] | (this.key[i] & 0xFF) << 8 * (i & 3);
        }
        for (i = 0; i < 32; ++i) {
            int n = (i >> 2) + 8;
            w[n] = w[n] | (this.iv[i] & 0xFF) << 8 * (i & 3);
        }
        for (i = 16; i < 2560; ++i) {
            int x = w[i - 2];
            int y = w[i - 15];
            w[i] = (HC256Engine.rotateRight(x, 17) ^ HC256Engine.rotateRight(x, 19) ^ x >>> 10) + w[i - 7] + (HC256Engine.rotateRight(y, 7) ^ HC256Engine.rotateRight(y, 18) ^ y >>> 3) + w[i - 16] + i;
        }
        System.arraycopy(w, 512, this.p, 0, 1024);
        System.arraycopy(w, 1536, this.q, 0, 1024);
        for (i = 0; i < 4096; ++i) {
            this.step();
        }
        this.cnt = 0;
    }

    @Override
    public String getAlgorithmName() {
        return "HC-256";
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        CipherParameters keyParam = params;
        if (params instanceof ParametersWithIV) {
            this.iv = ((ParametersWithIV)params).getIV();
            keyParam = ((ParametersWithIV)params).getParameters();
        } else {
            this.iv = new byte[0];
        }
        if (!(keyParam instanceof KeyParameter)) {
            throw new IllegalArgumentException("Invalid parameter passed to HC256 init - " + params.getClass().getName());
        }
        this.key = ((KeyParameter)keyParam).getKey();
        this.init();
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.key.length * 8, params, Utils.getPurpose(forEncryption)));
        this.initialised = true;
    }

    private byte getByte() {
        if (this.idx == 0) {
            int step = this.step();
            this.buf[0] = (byte)(step & 0xFF);
            this.buf[1] = (byte)((step >>= 8) & 0xFF);
            this.buf[2] = (byte)((step >>= 8) & 0xFF);
            this.buf[3] = (byte)((step >>= 8) & 0xFF);
        }
        byte ret = this.buf[this.idx];
        this.idx = this.idx + 1 & 3;
        return ret;
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        if (!this.initialised) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (inOff + len > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + len > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = 0; i < len; ++i) {
            out[outOff + i] = (byte)(in[inOff + i] ^ this.getByte());
        }
        return len;
    }

    @Override
    public void reset() {
        this.init();
    }

    @Override
    public byte returnByte(byte in) {
        return (byte)(in ^ this.getByte());
    }

    private static int rotateRight(int x, int bits) {
        return x >>> bits | x << -bits;
    }
}

