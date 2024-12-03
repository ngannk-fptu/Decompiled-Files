/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.util.DigestFactory;

public class PKCS5S2ParametersGenerator
extends PBEParametersGenerator {
    private Mac hMac;
    private byte[] state;

    public PKCS5S2ParametersGenerator() {
        this(DigestFactory.createSHA1());
    }

    public PKCS5S2ParametersGenerator(Digest digest) {
        this.hMac = new HMac(digest);
        this.state = new byte[this.hMac.getMacSize()];
    }

    private void F(byte[] S, int c, byte[] iBuf, byte[] out, int outOff) {
        if (c == 0) {
            throw new IllegalArgumentException("iteration count must be at least 1.");
        }
        if (S != null) {
            this.hMac.update(S, 0, S.length);
        }
        this.hMac.update(iBuf, 0, iBuf.length);
        this.hMac.doFinal(this.state, 0);
        System.arraycopy(this.state, 0, out, outOff, this.state.length);
        for (int count = 1; count < c; ++count) {
            this.hMac.update(this.state, 0, this.state.length);
            this.hMac.doFinal(this.state, 0);
            for (int j = 0; j != this.state.length; ++j) {
                int n = outOff + j;
                out[n] = (byte)(out[n] ^ this.state[j]);
            }
        }
    }

    private byte[] generateDerivedKey(int dkLen) {
        int hLen = this.hMac.getMacSize();
        int l = (dkLen + hLen - 1) / hLen;
        byte[] iBuf = new byte[4];
        byte[] outBytes = new byte[l * hLen];
        int outPos = 0;
        KeyParameter param = new KeyParameter(this.password);
        this.hMac.init(param);
        for (int i = 1; i <= l; ++i) {
            int n;
            int pos = 3;
            do {
                n = pos--;
            } while ((iBuf[n] = (byte)(iBuf[n] + 1)) == 0);
            this.F(this.salt, this.iterationCount, iBuf, outBytes, outPos);
            outPos += hLen;
        }
        return outBytes;
    }

    @Override
    public CipherParameters generateDerivedParameters(int keySize) {
        byte[] dKey = this.generateDerivedKey(keySize /= 8);
        return new KeyParameter(dKey, 0, keySize);
    }

    @Override
    public CipherParameters generateDerivedParameters(int keySize, int ivSize) {
        byte[] dKey = this.generateDerivedKey((keySize /= 8) + (ivSize /= 8));
        return new ParametersWithIV(new KeyParameter(dKey, 0, keySize), dKey, keySize, ivSize);
    }

    @Override
    public CipherParameters generateDerivedMacParameters(int keySize) {
        return this.generateDerivedParameters(keySize);
    }
}

