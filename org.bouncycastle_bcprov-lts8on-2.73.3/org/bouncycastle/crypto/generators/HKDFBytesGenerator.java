/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;

public class HKDFBytesGenerator
implements DerivationFunction {
    private HMac hMacHash;
    private int hashLen;
    private byte[] info;
    private byte[] currentT;
    private int generatedBytes;

    public HKDFBytesGenerator(Digest hash) {
        this.hMacHash = new HMac(hash);
        this.hashLen = hash.getDigestSize();
    }

    @Override
    public void init(DerivationParameters param) {
        if (!(param instanceof HKDFParameters)) {
            throw new IllegalArgumentException("HKDF parameters required for HKDFBytesGenerator");
        }
        HKDFParameters params = (HKDFParameters)param;
        if (params.skipExtract()) {
            this.hMacHash.init(new KeyParameter(params.getIKM()));
        } else {
            this.hMacHash.init(new KeyParameter(this.extractPRK(params.getSalt(), params.getIKM())));
        }
        this.info = params.getInfo();
        this.generatedBytes = 0;
        this.currentT = new byte[this.hashLen];
    }

    public byte[] extractPRK(byte[] salt, byte[] ikm) {
        if (salt == null) {
            this.hMacHash.init(new KeyParameter(new byte[this.hashLen]));
        } else {
            this.hMacHash.init(new KeyParameter(salt));
        }
        this.hMacHash.update(ikm, 0, ikm.length);
        byte[] prk = new byte[this.hashLen];
        this.hMacHash.doFinal(prk, 0);
        return prk;
    }

    private void expandNext() throws DataLengthException {
        int n = this.generatedBytes / this.hashLen + 1;
        if (n >= 256) {
            throw new DataLengthException("HKDF cannot generate more than 255 blocks of HashLen size");
        }
        if (this.generatedBytes != 0) {
            this.hMacHash.update(this.currentT, 0, this.hashLen);
        }
        this.hMacHash.update(this.info, 0, this.info.length);
        this.hMacHash.update((byte)n);
        this.hMacHash.doFinal(this.currentT, 0);
    }

    public Digest getDigest() {
        return this.hMacHash.getUnderlyingDigest();
    }

    @Override
    public int generateBytes(byte[] out, int outOff, int len) throws DataLengthException, IllegalArgumentException {
        if (this.generatedBytes + len > 255 * this.hashLen) {
            throw new DataLengthException("HKDF may only be used for 255 * HashLen bytes of output");
        }
        if (this.generatedBytes % this.hashLen == 0) {
            this.expandNext();
        }
        int toGenerate = len;
        int posInT = this.generatedBytes % this.hashLen;
        int leftInT = this.hashLen - this.generatedBytes % this.hashLen;
        int toCopy = Math.min(leftInT, toGenerate);
        System.arraycopy(this.currentT, posInT, out, outOff, toCopy);
        this.generatedBytes += toCopy;
        toGenerate -= toCopy;
        outOff += toCopy;
        while (toGenerate > 0) {
            this.expandNext();
            toCopy = Math.min(this.hashLen, toGenerate);
            System.arraycopy(this.currentT, 0, out, outOff, toCopy);
            this.generatedBytes += toCopy;
            toGenerate -= toCopy;
            outOff += toCopy;
        }
        return len;
    }
}

