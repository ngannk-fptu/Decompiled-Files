/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.util.DigestFactory;

public class OpenSSLPBEParametersGenerator
extends PBEParametersGenerator {
    private final Digest digest;

    public OpenSSLPBEParametersGenerator() {
        this(DigestFactory.createMD5());
    }

    public OpenSSLPBEParametersGenerator(Digest digest) {
        this.digest = digest;
    }

    public void init(byte[] password, byte[] salt) {
        super.init(password, salt, 1);
    }

    private byte[] generateDerivedKey(int bytesNeeded) {
        byte[] buf = new byte[this.digest.getDigestSize()];
        byte[] key = new byte[bytesNeeded];
        int offset = 0;
        while (true) {
            this.digest.update(this.password, 0, this.password.length);
            this.digest.update(this.salt, 0, this.salt.length);
            this.digest.doFinal(buf, 0);
            int len = bytesNeeded > buf.length ? buf.length : bytesNeeded;
            System.arraycopy(buf, 0, key, offset, len);
            offset += len;
            if ((bytesNeeded -= len) == 0) break;
            this.digest.reset();
            this.digest.update(buf, 0, buf.length);
        }
        return key;
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

