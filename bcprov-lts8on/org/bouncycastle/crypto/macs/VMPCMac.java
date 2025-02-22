/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class VMPCMac
implements Mac {
    private byte g;
    private byte n = 0;
    private byte[] P = null;
    private byte s = 0;
    private byte[] T;
    private byte[] workingIV;
    private byte[] workingKey;
    private byte x1;
    private byte x2;
    private byte x3;
    private byte x4;

    @Override
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        byte temp;
        for (int r = 1; r < 25; ++r) {
            this.s = this.P[this.s + this.P[this.n & 0xFF] & 0xFF];
            this.x4 = this.P[this.x4 + this.x3 + r & 0xFF];
            this.x3 = this.P[this.x3 + this.x2 + r & 0xFF];
            this.x2 = this.P[this.x2 + this.x1 + r & 0xFF];
            this.x1 = this.P[this.x1 + this.s + r & 0xFF];
            this.T[this.g & 0x1F] = (byte)(this.T[this.g & 0x1F] ^ this.x1);
            this.T[this.g + 1 & 0x1F] = (byte)(this.T[this.g + 1 & 0x1F] ^ this.x2);
            this.T[this.g + 2 & 0x1F] = (byte)(this.T[this.g + 2 & 0x1F] ^ this.x3);
            this.T[this.g + 3 & 0x1F] = (byte)(this.T[this.g + 3 & 0x1F] ^ this.x4);
            this.g = (byte)(this.g + 4 & 0x1F);
            temp = this.P[this.n & 0xFF];
            this.P[this.n & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = temp;
            this.n = (byte)(this.n + 1 & 0xFF);
        }
        for (int m = 0; m < 768; ++m) {
            this.s = this.P[this.s + this.P[m & 0xFF] + this.T[m & 0x1F] & 0xFF];
            temp = this.P[m & 0xFF];
            this.P[m & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = temp;
        }
        byte[] M = new byte[20];
        for (int i = 0; i < 20; ++i) {
            this.s = this.P[this.s + this.P[i & 0xFF] & 0xFF];
            M[i] = this.P[this.P[this.P[this.s & 0xFF] & 0xFF] + 1 & 0xFF];
            byte temp2 = this.P[i & 0xFF];
            this.P[i & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = temp2;
        }
        System.arraycopy(M, 0, out, outOff, M.length);
        this.reset();
        return M.length;
    }

    @Override
    public String getAlgorithmName() {
        return "VMPC-MAC";
    }

    @Override
    public int getMacSize() {
        return 20;
    }

    @Override
    public void init(CipherParameters params) throws IllegalArgumentException {
        if (!(params instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("VMPC-MAC Init parameters must include an IV");
        }
        ParametersWithIV ivParams = (ParametersWithIV)params;
        KeyParameter key = (KeyParameter)ivParams.getParameters();
        if (!(ivParams.getParameters() instanceof KeyParameter)) {
            throw new IllegalArgumentException("VMPC-MAC Init parameters must include a key");
        }
        this.workingIV = ivParams.getIV();
        if (this.workingIV == null || this.workingIV.length < 1 || this.workingIV.length > 768) {
            throw new IllegalArgumentException("VMPC-MAC requires 1 to 768 bytes of IV");
        }
        this.workingKey = key.getKey();
        this.reset();
    }

    private void initKey(byte[] keyBytes, byte[] ivBytes) {
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
    public void reset() {
        this.initKey(this.workingKey, this.workingIV);
        this.n = 0;
        this.x4 = 0;
        this.x3 = 0;
        this.x2 = 0;
        this.x1 = 0;
        this.g = 0;
        this.T = new byte[32];
        for (int i = 0; i < 32; ++i) {
            this.T[i] = 0;
        }
    }

    @Override
    public void update(byte in) throws IllegalStateException {
        this.s = this.P[this.s + this.P[this.n & 0xFF] & 0xFF];
        byte c = (byte)(in ^ this.P[this.P[this.P[this.s & 0xFF] & 0xFF] + 1 & 0xFF]);
        this.x4 = this.P[this.x4 + this.x3 & 0xFF];
        this.x3 = this.P[this.x3 + this.x2 & 0xFF];
        this.x2 = this.P[this.x2 + this.x1 & 0xFF];
        this.x1 = this.P[this.x1 + this.s + c & 0xFF];
        this.T[this.g & 0x1F] = (byte)(this.T[this.g & 0x1F] ^ this.x1);
        this.T[this.g + 1 & 0x1F] = (byte)(this.T[this.g + 1 & 0x1F] ^ this.x2);
        this.T[this.g + 2 & 0x1F] = (byte)(this.T[this.g + 2 & 0x1F] ^ this.x3);
        this.T[this.g + 3 & 0x1F] = (byte)(this.T[this.g + 3 & 0x1F] ^ this.x4);
        this.g = (byte)(this.g + 4 & 0x1F);
        byte temp = this.P[this.n & 0xFF];
        this.P[this.n & 0xFF] = this.P[this.s & 0xFF];
        this.P[this.s & 0xFF] = temp;
        this.n = (byte)(this.n + 1 & 0xFF);
    }

    @Override
    public void update(byte[] in, int inOff, int len) throws DataLengthException, IllegalStateException {
        if (inOff + len > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        for (int i = 0; i < len; ++i) {
            this.update(in[inOff + i]);
        }
    }
}

