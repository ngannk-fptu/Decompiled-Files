/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.params.DSAKeyParameters;
import org.bouncycastle.crypto.params.DSAParameters;

public class DSAPublicKeyParameters
extends DSAKeyParameters {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private static final BigInteger TWO = BigInteger.valueOf(2L);
    private BigInteger y;

    public DSAPublicKeyParameters(BigInteger y, DSAParameters params) {
        super(false, params);
        this.y = this.validate(y, params);
    }

    private BigInteger validate(BigInteger y, DSAParameters params) {
        if (params != null) {
            if (TWO.compareTo(y) <= 0 && params.getP().subtract(TWO).compareTo(y) >= 0 && ONE.equals(y.modPow(params.getQ(), params.getP()))) {
                return y;
            }
            throw new IllegalArgumentException("y value does not appear to be in correct group");
        }
        return y;
    }

    public BigInteger getY() {
        return this.y;
    }
}

