/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;

public class KEMGenerateSpec
implements AlgorithmParameterSpec {
    private final PublicKey publicKey;
    private final String keyAlgorithmName;

    public KEMGenerateSpec(PublicKey publicKey, String keyAlgorithmName) {
        this.publicKey = publicKey;
        this.keyAlgorithmName = keyAlgorithmName;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public String getKeyAlgorithmName() {
        return this.keyAlgorithmName;
    }
}

