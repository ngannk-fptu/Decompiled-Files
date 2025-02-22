/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.math.BigInteger;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;

public class DHExtendedPublicKeySpec
extends DHPublicKeySpec {
    private final DHParameterSpec params;

    public DHExtendedPublicKeySpec(BigInteger y, DHParameterSpec params) {
        super(y, params.getP(), params.getG());
        this.params = params;
    }

    public DHParameterSpec getParams() {
        return this.params;
    }
}

