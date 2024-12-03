/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.spec;

import java.math.BigInteger;
import org.bouncycastle.jce.spec.ElGamalKeySpec;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;

public class ElGamalPublicKeySpec
extends ElGamalKeySpec {
    private BigInteger y;

    public ElGamalPublicKeySpec(BigInteger y, ElGamalParameterSpec spec) {
        super(spec);
        this.y = y;
    }

    public BigInteger getY() {
        return this.y;
    }
}

