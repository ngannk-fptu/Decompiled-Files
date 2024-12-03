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

public class RC4Engine
implements StreamCipher {
    private static final int STATE_LENGTH = 256;
    private byte[] engineState = null;
    private int x = 0;
    private int y = 0;
    private byte[] workingKey = null;
    private boolean forEncryption;

    public RC4Engine() {
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 20));
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) {
        if (params instanceof KeyParameter) {
            this.workingKey = ((KeyParameter)params).getKey();
            this.forEncryption = forEncryption;
            this.setKey(this.workingKey);
            CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 20, params, Utils.getPurpose(forEncryption)));
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to RC4 init - " + params.getClass().getName());
    }

    @Override
    public String getAlgorithmName() {
        return "RC4";
    }

    @Override
    public byte returnByte(byte in) {
        this.x = this.x + 1 & 0xFF;
        this.y = this.engineState[this.x] + this.y & 0xFF;
        byte tmp = this.engineState[this.x];
        this.engineState[this.x] = this.engineState[this.y];
        this.engineState[this.y] = tmp;
        return (byte)(in ^ this.engineState[this.engineState[this.x] + this.engineState[this.y] & 0xFF]);
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) {
        if (inOff + len > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + len > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = 0; i < len; ++i) {
            this.x = this.x + 1 & 0xFF;
            this.y = this.engineState[this.x] + this.y & 0xFF;
            byte tmp = this.engineState[this.x];
            this.engineState[this.x] = this.engineState[this.y];
            this.engineState[this.y] = tmp;
            out[i + outOff] = (byte)(in[i + inOff] ^ this.engineState[this.engineState[this.x] + this.engineState[this.y] & 0xFF]);
        }
        return len;
    }

    @Override
    public void reset() {
        this.setKey(this.workingKey);
    }

    private void setKey(byte[] keyBytes) {
        this.workingKey = keyBytes;
        this.x = 0;
        this.y = 0;
        if (this.engineState == null) {
            this.engineState = new byte[256];
        }
        for (int i = 0; i < 256; ++i) {
            this.engineState[i] = (byte)i;
        }
        int i1 = 0;
        int i2 = 0;
        for (int i = 0; i < 256; ++i) {
            i2 = (keyBytes[i1] & 0xFF) + this.engineState[i] + i2 & 0xFF;
            byte tmp = this.engineState[i];
            this.engineState[i] = this.engineState[i2];
            this.engineState[i2] = tmp;
            i1 = (i1 + 1) % keyBytes.length;
        }
    }
}

