/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class PKCS5S1ParametersGenerator
extends PBEParametersGenerator {
    private Digest digest;

    public PKCS5S1ParametersGenerator(Digest digest) {
        this.digest = digest;
    }

    private byte[] generateDerivedKey() {
        byte[] digestBytes = new byte[this.digest.getDigestSize()];
        this.digest.update(this.password, 0, this.password.length);
        this.digest.update(this.salt, 0, this.salt.length);
        this.digest.doFinal(digestBytes, 0);
        for (int i = 1; i < this.iterationCount; ++i) {
            this.digest.update(digestBytes, 0, digestBytes.length);
            this.digest.doFinal(digestBytes, 0);
        }
        return digestBytes;
    }

    @Override
    public CipherParameters generateDerivedParameters(int keySize) {
        if ((keySize /= 8) > this.digest.getDigestSize()) {
            throw new IllegalArgumentException("Can't generate a derived key " + keySize + " bytes long.");
        }
        byte[] dKey = this.generateDerivedKey();
        return new KeyParameter(dKey, 0, keySize);
    }

    @Override
    public CipherParameters generateDerivedParameters(int keySize, int ivSize) {
        if ((keySize /= 8) + (ivSize /= 8) > this.digest.getDigestSize()) {
            throw new IllegalArgumentException("Can't generate a derived key " + (keySize + ivSize) + " bytes long.");
        }
        byte[] dKey = this.generateDerivedKey();
        return new ParametersWithIV(new KeyParameter(dKey, 0, keySize), dKey, keySize, ivSize);
    }

    @Override
    public CipherParameters generateDerivedMacParameters(int keySize) {
        return this.generateDerivedParameters(keySize);
    }
}

