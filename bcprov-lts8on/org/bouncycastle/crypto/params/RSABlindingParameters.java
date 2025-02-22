/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

public class RSABlindingParameters
implements CipherParameters {
    private RSAKeyParameters publicKey;
    private BigInteger blindingFactor;

    public RSABlindingParameters(RSAKeyParameters publicKey, BigInteger blindingFactor) {
        if (publicKey instanceof RSAPrivateCrtKeyParameters) {
            throw new IllegalArgumentException("RSA parameters should be for a public key");
        }
        this.publicKey = publicKey;
        this.blindingFactor = blindingFactor;
    }

    public RSAKeyParameters getPublicKey() {
        return this.publicKey;
    }

    public BigInteger getBlindingFactor() {
        return this.blindingFactor;
    }
}

