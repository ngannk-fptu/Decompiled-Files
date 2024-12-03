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

public class HC128Engine
implements StreamCipher {
    private int[] p = new int[512];
    private int[] q = new int[512];
    private int cnt = 0;
    private byte[] key;
    private byte[] iv;
    private boolean initialised;
    private byte[] buf = new byte[4];
    private int idx = 0;

    private static int f1(int x) {
        return HC128Engine.rotateRight(x, 7) ^ HC128Engine.rotateRight(x, 18) ^ x >>> 3;
    }

    private static int f2(int x) {
        return HC128Engine.rotateRight(x, 17) ^ HC128Engine.rotateRight(x, 19) ^ x >>> 10;
    }

    private int g1(int x, int y, int z) {
        return (HC128Engine.rotateRight(x, 10) ^ HC128Engine.rotateRight(z, 23)) + HC128Engine.rotateRight(y, 8);
    }

    private int g2(int x, int y, int z) {
        return (HC128Engine.rotateLeft(x, 10) ^ HC128Engine.rotateLeft(z, 23)) + HC128Engine.rotateLeft(y, 8);
    }

    private static int rotateLeft(int x, int bits) {
        return x << bits | x >>> -bits;
    }

    private static int rotateRight(int x, int bits) {
        return x >>> bits | x << -bits;
    }

    private int h1(int x) {
        return this.q[x & 0xFF] + this.q[(x >> 16 & 0xFF) + 256];
    }

    private int h2(int x) {
        return this.p[x & 0xFF] + this.p[(x >> 16 & 0xFF) + 256];
    }

    private static int mod1024(int x) {
        return x & 0x3FF;
    }

    private static int mod512(int x) {
        return x & 0x1FF;
    }

    private static int dim(int x, int y) {
        return HC128Engine.mod512(x - y);
    }

    private int step() {
        int ret;
        int j = HC128Engine.mod512(this.cnt);
        if (this.cnt < 512) {
            int n = j;
            this.p[n] = this.p[n] + this.g1(this.p[HC128Engine.dim(j, 3)], this.p[HC128Engine.dim(j, 10)], this.p[HC128Engine.dim(j, 511)]);
            ret = this.h1(this.p[HC128Engine.dim(j, 12)]) ^ this.p[j];
        } else {
            int n = j;
            this.q[n] = this.q[n] + this.g2(this.q[HC128Engine.dim(j, 3)], this.q[HC128Engine.dim(j, 10)], this.q[HC128Engine.dim(j, 511)]);
            ret = this.h2(this.q[HC128Engine.dim(j, 12)]) ^ this.q[j];
        }
        this.cnt = HC128Engine.mod1024(this.cnt + 1);
        return ret;
    }

    private void init() {
        int i;
        if (this.key.length != 16) {
            throw new IllegalArgumentException("The key must be 128 bits long");
        }
        if (this.iv.length != 16) {
            throw new IllegalArgumentException("The IV must be 128 bits long");
        }
        this.idx = 0;
        this.cnt = 0;
        int[] w = new int[1280];
        for (i = 0; i < 16; ++i) {
            int n = i >> 2;
            w[n] = w[n] | (this.key[i] & 0xFF) << 8 * (i & 3);
        }
        System.arraycopy(w, 0, w, 4, 4);
        for (i = 0; i < this.iv.length && i < 16; ++i) {
            int n = (i >> 2) + 8;
            w[n] = w[n] | (this.iv[i] & 0xFF) << 8 * (i & 3);
        }
        System.arraycopy(w, 8, w, 12, 4);
        for (i = 16; i < 1280; ++i) {
            w[i] = HC128Engine.f2(w[i - 2]) + w[i - 7] + HC128Engine.f1(w[i - 15]) + w[i - 16] + i;
        }
        System.arraycopy(w, 256, this.p, 0, 512);
        System.arraycopy(w, 768, this.q, 0, 512);
        for (i = 0; i < 512; ++i) {
            this.p[i] = this.step();
        }
        for (i = 0; i < 512; ++i) {
            this.q[i] = this.step();
        }
        this.cnt = 0;
    }

    @Override
    public String getAlgorithmName() {
        return "HC-128";
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        if (!(params instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("no IV passed");
        }
        this.iv = ((ParametersWithIV)params).getIV();
        CipherParameters keyParam = ((ParametersWithIV)params).getParameters();
        if (!(keyParam instanceof KeyParameter)) {
            throw new IllegalArgumentException("Invalid parameter passed to HC128 init - " + params.getClass().getName());
        }
        this.key = ((KeyParameter)keyParam).getKey();
        this.init();
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 128, params, Utils.getPurpose(forEncryption)));
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
}

