/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement.jpake;

import java.math.BigInteger;
import org.bouncycastle.crypto.agreement.jpake.JPAKEUtil;

public class JPAKEPrimeOrderGroup {
    private final BigInteger p;
    private final BigInteger q;
    private final BigInteger g;

    public JPAKEPrimeOrderGroup(BigInteger p, BigInteger q, BigInteger g) {
        this(p, q, g, false);
    }

    JPAKEPrimeOrderGroup(BigInteger p, BigInteger q, BigInteger g, boolean skipChecks) {
        JPAKEUtil.validateNotNull(p, "p");
        JPAKEUtil.validateNotNull(q, "q");
        JPAKEUtil.validateNotNull(g, "g");
        if (!skipChecks) {
            if (!p.subtract(JPAKEUtil.ONE).mod(q).equals(JPAKEUtil.ZERO)) {
                throw new IllegalArgumentException("p-1 must be evenly divisible by q");
            }
            if (g.compareTo(BigInteger.valueOf(2L)) == -1 || g.compareTo(p.subtract(JPAKEUtil.ONE)) == 1) {
                throw new IllegalArgumentException("g must be in [2, p-1]");
            }
            if (!g.modPow(q, p).equals(JPAKEUtil.ONE)) {
                throw new IllegalArgumentException("g^q mod p must equal 1");
            }
            if (!p.isProbablePrime(20)) {
                throw new IllegalArgumentException("p must be prime");
            }
            if (!q.isProbablePrime(20)) {
                throw new IllegalArgumentException("q must be prime");
            }
        }
        this.p = p;
        this.q = q;
        this.g = g;
    }

    public BigInteger getP() {
        return this.p;
    }

    public BigInteger getQ() {
        return this.q;
    }

    public BigInteger getG() {
        return this.g;
    }
}

