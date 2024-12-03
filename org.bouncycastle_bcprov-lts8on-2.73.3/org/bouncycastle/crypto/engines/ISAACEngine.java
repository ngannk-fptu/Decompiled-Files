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
import org.bouncycastle.util.Pack;

public class ISAACEngine
implements StreamCipher {
    private final int sizeL = 8;
    private final int stateArraySize = 256;
    private int[] engineState = null;
    private int[] results = null;
    private int a = 0;
    private int b = 0;
    private int c = 0;
    private int index = 0;
    private byte[] keyStream = new byte[1024];
    private byte[] workingKey = null;
    private boolean initialised = false;

    @Override
    public void init(boolean forEncryption, CipherParameters params) {
        if (!(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to ISAAC init - " + params.getClass().getName());
        }
        KeyParameter p = (KeyParameter)params;
        byte[] key = p.getKey();
        this.setKey(key);
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), key.length < 32 ? key.length * 8 : 256, params, Utils.getPurpose(forEncryption)));
    }

    @Override
    public byte returnByte(byte in) {
        if (this.index == 0) {
            this.isaac();
            this.keyStream = Pack.intToBigEndian(this.results);
        }
        byte out = (byte)(this.keyStream[this.index] ^ in);
        this.index = this.index + 1 & 0x3FF;
        return out;
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) {
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
            if (this.index == 0) {
                this.isaac();
                this.keyStream = Pack.intToBigEndian(this.results);
            }
            out[i + outOff] = (byte)(this.keyStream[this.index] ^ in[i + inOff]);
            this.index = this.index + 1 & 0x3FF;
        }
        return len;
    }

    @Override
    public String getAlgorithmName() {
        return "ISAAC";
    }

    @Override
    public void reset() {
        this.setKey(this.workingKey);
    }

    private void setKey(byte[] keyBytes) {
        int i;
        this.workingKey = keyBytes;
        if (this.engineState == null) {
            this.engineState = new int[256];
        }
        if (this.results == null) {
            this.results = new int[256];
        }
        for (i = 0; i < 256; ++i) {
            this.results[i] = 0;
            this.engineState[i] = 0;
        }
        this.c = 0;
        this.b = 0;
        this.a = 0;
        this.index = 0;
        byte[] t = new byte[keyBytes.length + (keyBytes.length & 3)];
        System.arraycopy(keyBytes, 0, t, 0, keyBytes.length);
        for (i = 0; i < t.length; i += 4) {
            this.results[i >>> 2] = Pack.littleEndianToInt(t, i);
        }
        int[] abcdefgh = new int[8];
        for (i = 0; i < 8; ++i) {
            abcdefgh[i] = -1640531527;
        }
        for (i = 0; i < 4; ++i) {
            this.mix(abcdefgh);
        }
        for (i = 0; i < 2; ++i) {
            for (int j = 0; j < 256; j += 8) {
                int k;
                for (k = 0; k < 8; ++k) {
                    int n = k;
                    abcdefgh[n] = abcdefgh[n] + (i < 1 ? this.results[j + k] : this.engineState[j + k]);
                }
                this.mix(abcdefgh);
                for (k = 0; k < 8; ++k) {
                    this.engineState[j + k] = abcdefgh[k];
                }
            }
        }
        this.isaac();
        this.initialised = true;
    }

    private void isaac() {
        this.b += ++this.c;
        for (int i = 0; i < 256; ++i) {
            int y;
            int x = this.engineState[i];
            switch (i & 3) {
                case 0: {
                    this.a ^= this.a << 13;
                    break;
                }
                case 1: {
                    this.a ^= this.a >>> 6;
                    break;
                }
                case 2: {
                    this.a ^= this.a << 2;
                    break;
                }
                case 3: {
                    this.a ^= this.a >>> 16;
                }
            }
            this.a += this.engineState[i + 128 & 0xFF];
            this.engineState[i] = y = this.engineState[x >>> 2 & 0xFF] + this.a + this.b;
            this.results[i] = this.b = this.engineState[y >>> 10 & 0xFF] + x;
        }
    }

    private void mix(int[] x) {
        x[0] = x[0] ^ x[1] << 11;
        x[3] = x[3] + x[0];
        x[1] = x[1] + x[2];
        x[1] = x[1] ^ x[2] >>> 2;
        x[4] = x[4] + x[1];
        x[2] = x[2] + x[3];
        x[2] = x[2] ^ x[3] << 8;
        x[5] = x[5] + x[2];
        x[3] = x[3] + x[4];
        x[3] = x[3] ^ x[4] >>> 16;
        x[6] = x[6] + x[3];
        x[4] = x[4] + x[5];
        x[4] = x[4] ^ x[5] << 10;
        x[7] = x[7] + x[4];
        x[5] = x[5] + x[6];
        x[5] = x[5] ^ x[6] >>> 4;
        x[0] = x[0] + x[5];
        x[6] = x[6] + x[7];
        x[6] = x[6] ^ x[7] << 8;
        x[1] = x[1] + x[6];
        x[7] = x[7] + x[0];
        x[7] = x[7] ^ x[0] >>> 9;
        x[2] = x[2] + x[7];
        x[0] = x[0] + x[1];
    }
}

