/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.CramerShoupParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.util.BigIntegers;

public class CramerShoupParametersGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private int size;
    private int certainty;
    private SecureRandom random;

    public void init(int size, int certainty, SecureRandom random) {
        this.size = size;
        this.certainty = certainty;
        this.random = random;
    }

    public CramerShoupParameters generateParameters() {
        BigInteger[] safePrimes = ParametersHelper.generateSafePrimes(this.size, this.certainty, this.random);
        BigInteger q = safePrimes[1];
        BigInteger g1 = ParametersHelper.selectGenerator(q, this.random);
        BigInteger g2 = ParametersHelper.selectGenerator(q, this.random);
        while (g1.equals(g2)) {
            g2 = ParametersHelper.selectGenerator(q, this.random);
        }
        return new CramerShoupParameters(q, g1, g2, SHA256Digest.newInstance());
    }

    public CramerShoupParameters generateParameters(DHParameters dhParams) {
        BigInteger p = dhParams.getP();
        BigInteger g1 = dhParams.getG();
        BigInteger g2 = ParametersHelper.selectGenerator(p, this.random);
        while (g1.equals(g2)) {
            g2 = ParametersHelper.selectGenerator(p, this.random);
        }
        return new CramerShoupParameters(p, g1, g2, SHA256Digest.newInstance());
    }

    private static class ParametersHelper {
        private static final BigInteger TWO = BigInteger.valueOf(2L);

        private ParametersHelper() {
        }

        static BigInteger[] generateSafePrimes(int size, int certainty, SecureRandom random) {
            BigInteger q;
            BigInteger p;
            int qLength = size - 1;
            while (!(p = (q = BigIntegers.createRandomPrime(qLength, 2, random)).shiftLeft(1).add(ONE)).isProbablePrime(certainty) || certainty > 2 && !q.isProbablePrime(certainty)) {
            }
            return new BigInteger[]{p, q};
        }

        static BigInteger selectGenerator(BigInteger p, SecureRandom random) {
            BigInteger h;
            BigInteger g;
            BigInteger pMinusTwo = p.subtract(TWO);
            while ((g = (h = BigIntegers.createRandomInRange(TWO, pMinusTwo, random)).modPow(TWO, p)).equals(ONE)) {
            }
            return g;
        }
    }
}

