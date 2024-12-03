/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.BigIntegers;

class DHParametersHelper {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private static final BigInteger TWO = BigInteger.valueOf(2L);

    DHParametersHelper() {
    }

    static BigInteger[] generateSafePrimes(int size, int certainty, SecureRandom random) {
        BigInteger q;
        BigInteger p;
        int qLength = size - 1;
        int minWeight = size >>> 2;
        while (!(p = (q = BigIntegers.createRandomPrime(qLength, 2, random)).shiftLeft(1).add(ONE)).isProbablePrime(certainty) || certainty > 2 && !q.isProbablePrime(certainty - 2) || WNafUtil.getNafWeight(p) < minWeight) {
        }
        return new BigInteger[]{p, q};
    }

    static BigInteger selectGenerator(BigInteger p, BigInteger q, SecureRandom random) {
        BigInteger h;
        BigInteger g;
        BigInteger pMinusTwo = p.subtract(TWO);
        while ((g = (h = BigIntegers.createRandomInRange(TWO, pMinusTwo, random)).modPow(TWO, p)).equals(ONE)) {
        }
        return g;
    }
}

