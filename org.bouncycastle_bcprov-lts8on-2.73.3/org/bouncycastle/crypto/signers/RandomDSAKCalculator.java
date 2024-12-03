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

    @Override
    public boolean isDeterministic() {
        return false;
    }

    @Override
    public void init(BigInteger n, SecureRandom random) {
        this.q = n;
        this.random = random;
    }

    @Override
    public void init(BigInteger n, BigInteger d, byte[] message) {
        throw new IllegalStateException("Operation not supported");
    }

    @Override
    public BigInteger nextK() {
        BigInteger k;
        int qBitLength = this.q.bitLength();
        while ((k = BigIntegers.createRandomBigInteger(qBitLength, this.random)).equals(ZERO) || k.compareTo(this.q) >= 0) {
        }
        return k;
    }
}

