/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.spec;

import java.math.BigInteger;
import org.bouncycastle.jce.spec.ECKeySpec;
import org.bouncycastle.jce.spec.ECParameterSpec;

public class ECPrivateKeySpec
extends ECKeySpec {
    private BigInteger d;

    public ECPrivateKeySpec(BigInteger bigInteger, ECParameterSpec eCParameterSpec) {
        super(eCParameterSpec);
        this.d = bigInteger;
    }

    public BigInteger getD() {
        return this.d;
    }
}

