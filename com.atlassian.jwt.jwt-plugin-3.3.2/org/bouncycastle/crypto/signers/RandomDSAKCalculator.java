/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.util.BigIntegers;

public class RandomDSAKCalculator
implements DSAKCalculator {
    private static final BigInteger ZERO = BigInteger.valueOf(0L);
    private BigInteger q;
    private SecureRandom random;

    public boolean isDeterministic() {
        return false;
    }

    public void init(BigInteger bigInteger, SecureRandom secureRandom) {
        this.q = bigInteger;
        this.random = secureRandom;
    }

    public void init(BigInteger bigInteger, BigInteger bigInteger2, byte[] byArray) {
        throw new IllegalStateException("Operation not supported");
    }

    public BigInteger nextK() {
        BigInteger bigInteger;
        int n = this.q.bitLength();
        while ((bigInteger = BigIntegers.createRandomBigInteger(n, this.random)).equals(ZERO) || bigInteger.compareTo(this.q) >= 0) {
        }
        return bigInteger;
    }
}

