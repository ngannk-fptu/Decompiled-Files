/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.math.BigInteger;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;

public class DHExtendedPrivateKeySpec
extends DHPrivateKeySpec {
    private final DHParameterSpec params;

    public DHExtendedPrivateKeySpec(BigInteger x, DHParameterSpec params) {
        super(x, params.getP(), params.getG());
        this.params = params;
    }

    public DHParameterSpec getParams() {
        return this.params;
    }
}

