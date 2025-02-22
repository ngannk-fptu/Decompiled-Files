/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class RSAPrivateCrtKeyParameters
extends RSAKeyParameters {
    private BigInteger e;
    private BigInteger p;
    private BigInteger q;
    private BigInteger dP;
    private BigInteger dQ;
    private BigInteger qInv;

    public RSAPrivateCrtKeyParameters(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4, BigInteger bigInteger5, BigInteger bigInteger6, BigInteger bigInteger7, BigInteger bigInteger8) {
        super(true, bigInteger, bigInteger3);
        this.e = bigInteger2;
        this.p = bigInteger4;
        this.q = bigInteger5;
        this.dP = bigInteger6;
        this.dQ = bigInteger7;
        this.qInv = bigInteger8;
    }

    public BigInteger getPublicExponent() {
        return this.e;
    }

    public BigInteger getP() {
        return this.p;
    }

    public BigInteger getQ() {
        return this.q;
    }

    public BigInteger getDP() {
        return this.dP;
    }

    public BigInteger getDQ() {
        return this.dQ;
    }

    public BigInteger getQInv() {
        return this.qInv;
    }
}

