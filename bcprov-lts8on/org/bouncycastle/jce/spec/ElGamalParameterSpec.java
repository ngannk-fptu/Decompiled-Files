/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.spec;

import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;

public class ElGamalParameterSpec
implements AlgorithmParameterSpec {
    private BigInteger p;
    private BigInteger g;

    public ElGamalParameterSpec(BigInteger p, BigInteger g) {
        this.p = p;
        this.g = g;
    }

    public BigInteger getP() {
        return this.p;
    }

    public BigInteger getG() {
        return this.g;
    }
}

