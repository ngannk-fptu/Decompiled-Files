/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.PrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class KEMExtractSpec
implements AlgorithmParameterSpec {
    private final PrivateKey privateKey;
    private final byte[] encapsulation;
    private final String keyAlgorithmName;

    public KEMExtractSpec(PrivateKey privateKey, byte[] encapsulation, String keyAlgorithmName) {
        this.privateKey = privateKey;
        this.encapsulation = Arrays.clone(encapsulation);
        this.keyAlgorithmName = keyAlgorithmName;
    }

    public byte[] getEncapsulation() {
        return Arrays.clone(this.encapsulation);
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public String getKeyAlgorithmName() {
        return this.keyAlgorithmName;
    }
}

