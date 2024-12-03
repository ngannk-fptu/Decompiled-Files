/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.spec;

import java.math.BigInteger;
import org.bouncycastle.jce.spec.ElGamalKeySpec;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;

public class ElGamalPrivateKeySpec
extends ElGamalKeySpec {
    private BigInteger x;

    public ElGamalPrivateKeySpec(BigInteger x, ElGamalParameterSpec spec) {
        super(spec);
        this.x = x;
    }

    public BigInteger getX() {
        return this.x;
    }
}

