/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.math.Primes;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.BigIntegers;

public class RSAKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private RSAKeyGenerationParameters param;

    @Override
    public void init(KeyGenerationParameters param) {
        this.param = (RSAKeyGenerationParameters)param;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSAKeyGen", ConstraintUtils.bitsOfSecurityForFF(param.getStrength()), null, CryptoServicePurpose.KEYGEN));
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        AsymmetricCipherKeyPair result = null;
        boolean done = false;
        int strength = this.param.getStrength();
        int pbitlength = (strength + 1) / 2;
        int qbitlength = strength - pbitlength;
        int mindiffbits = strength / 2 - 100;
        if (mindiffbits < strength / 3) {
            mindiffbits = strength / 3;
        }
        int minWeight = strength >> 2;
        BigInteger dLowerBound = BigInteger.valueOf(2L).pow(strength / 2);
        BigInteger squaredBound = ONE.shiftLeft(strength - 1);
        BigInteger minDiff = ONE.shiftLeft(mindiffbits);
        while (!done) {
            BigInteger qSub1;
            BigInteger pSub1;
            BigInteger lcm;
            BigInteger d;
            BigInteger gcd;
            BigInteger n;
            BigInteger q;
            BigInteger e = this.param.getPublicExponent();
            BigInteger p = this.chooseRandomPrime(pbitlength, e, squaredBound);
            while (true) {
                BigInteger diff;
                if ((diff = (q = this.chooseRandomPrime(qbitlength, e, squaredBound)).subtract(p).abs()).bitLength() < mindiffbits || diff.compareTo(minDiff) <= 0) {
                    continue;
                }
                n = p.multiply(q);
                if (n.bitLength() != strength) {
                    p = p.max(q);
                    continue;
                }
                if (WNafUtil.getNafWeight(n) >= minWeight) break;
                p = this.chooseRandomPrime(pbitlength, e, squaredBound);
            }
            if (p.compareTo(q) < 0) {
                gcd = p;
                p = q;
                q = gcd;
            }
            if ((d = e.modInverse(lcm = (pSub1 = p.subtract(ONE)).divide(gcd = pSub1.gcd(qSub1 = q.subtract(ONE))).multiply(qSub1))).compareTo(dLowerBound) <= 0) continue;
            done = true;
            BigInteger dP = d.remainder(pSub1);
            BigInteger dQ = d.remainder(qSub1);
            BigInteger qInv = BigIntegers.modOddInverse(p, q);
            result = new AsymmetricCipherKeyPair(new RSAKeyParameters(false, n, e, true), new RSAPrivateCrtKeyParameters(n, e, d, p, q, dP, dQ, qInv, true));
        }
        return result;
    }

    protected BigInteger chooseRandomPrime(int bitlength, BigInteger e, BigInteger sqrdBound) {
        for (int i = 0; i != 5 * bitlength; ++i) {
            BigInteger p = BigIntegers.createRandomPrime(bitlength, 1, this.param.getRandom());
            if (p.mod(e).equals(ONE) || p.multiply(p).compareTo(sqrdBound) < 0 || !this.isProbablePrime(p) || !e.gcd(p.subtract(ONE)).equals(ONE)) continue;
            return p;
        }
        throw new IllegalStateException("unable to generate prime number for RSA key");
    }

    protected boolean isProbablePrime(BigInteger x) {
        int iterations = RSAKeyPairGenerator.getNumberOfIterations(x.bitLength(), this.param.getCertainty());
        return !Primes.hasAnySmallFactors(x) && Primes.isMRProbablePrime(x, this.param.getRandom(), iterations);
    }

    private static int getNumberOfIterations(int bits, int certainty) {
        if (bits >= 1536) {
            return certainty <= 100 ? 3 : (certainty <= 128 ? 4 : 4 + (certainty - 128 + 1) / 2);
        }
        if (bits >= 1024) {
            return certainty <= 100 ? 4 : (certainty <= 112 ? 5 : 5 + (certainty - 112 + 1) / 2);
        }
        if (bits >= 512) {
            return certainty <= 80 ? 5 : (certainty <= 100 ? 7 : 7 + (certainty - 100 + 1) / 2);
        }
        return certainty <= 80 ? 40 : 40 + (certainty - 80 + 1) / 2;
    }
}

