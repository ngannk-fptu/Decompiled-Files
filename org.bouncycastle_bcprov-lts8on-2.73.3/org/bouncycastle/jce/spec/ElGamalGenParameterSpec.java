/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.spec;

import java.security.spec.AlgorithmParameterSpec;

public class ElGamalGenParameterSpec
implements AlgorithmParameterSpec {
    private int primeSize;

    public ElGamalGenParameterSpec(int primeSize) {
        this.primeSize = primeSize;
    }

    public int getPrimeSize() {
        return this.primeSize;
    }
}

