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

public class VMPCEngine
implements StreamCipher {
    protected byte n = 0;
    protected byte[] P = null;
    protected byte s = 0;
    protected byte[] workingIV;
    protected byte[] workingKey;

    @Override
    public String getAlgorithmName() {
        return "VMPC";
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) {
        if (!(params instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("VMPC init parameters must include an IV");
        }
        ParametersWithIV ivParams = (ParametersWithIV)params;
        if (!(ivParams.getParameters() instanceof KeyParameter)) {
            throw new IllegalArgumentException("VMPC init parameters must include a key");
        }
        KeyParameter key = (KeyParameter)ivParams.getParameters();
        this.workingIV = ivParams.getIV();
        if (this.workingIV == null || this.workingIV.length < 1 || this.workingIV.length > 768) {
            throw new IllegalArgumentException("VMPC requires 1 to 768 bytes of IV");
        }
        this.workingKey = key.getKey();
        this.initKey(this.workingKey, this.workingIV);
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.workingKey.length >= 32 ? 256 : this.workingKey.length * 8, params, Utils.getPurpose(forEncryption)));
    }

    protected void initKey(byte[] keyBytes, byte[] ivBytes) {
        byte temp;
        int m;
        this.s = 0;
        this.P = new byte[256];
        for (int i = 0; i < 256; ++i) {
            this.P[i] = (byte)i;
        }
        for (m = 0; m < 768; ++m) {
            this.s = this.P[this.s + this.P[m & 0xFF] + keyBytes[m % keyBytes.length] & 0xFF];
            temp = this.P[m & 0xFF];
            this.P[m & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = temp;
        }
        for (m = 0; m < 768; ++m) {
            this.s = this.P[this.s + this.P[m & 0xFF] + ivBytes[m % ivBytes.length] & 0xFF];
            temp = this.P[m & 0xFF];
            this.P[m & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = temp;
        }
        this.n = 0;
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
            this.s = this.P[this.s + this.P[this.n & 0xFF] & 0xFF];
            byte z = this.P[this.P[this.P[this.s & 0xFF] & 0xFF] + 1 & 0xFF];
            byte temp = this.P[this.n & 0xFF];
            this.P[this.n & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = temp;
            this.n = (byte)(this.n + 1 & 0xFF);
            out[i + outOff] = (byte)(in[i + inOff] ^ z);
        }
        return len;
    }

    @Override
    public void reset() {
        this.initKey(this.workingKey, this.workingIV);
    }

    @Override
    public byte returnByte(byte in) {
        this.s = this.P[this.s + this.P[this.n & 0xFF] & 0xFF];
        byte z = this.P[this.P[this.P[this.s & 0xFF] & 0xFF] + 1 & 0xFF];
        byte temp = this.P[this.n & 0xFF];
        this.P[this.n & 0xFF] = this.P[this.s & 0xFF];
        this.P[this.s & 0xFF] = temp;
        this.n = (byte)(this.n + 1 & 0xFF);
        return (byte)(in ^ z);
    }
}

