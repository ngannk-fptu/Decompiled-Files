/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class RSABlindingFactorGenerator {
    private static BigInteger ZERO = BigInteger.valueOf(0L);
    private static BigInteger ONE = BigInteger.valueOf(1L);
    private RSAKeyParameters key;
    private SecureRandom random;

    public void init(CipherParameters param) {
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom)param;
            this.key = (RSAKeyParameters)rParam.getParameters();
            this.random = rParam.getRandom();
        } else {
            this.key = (RSAKeyParameters)param;
            this.random = CryptoServicesRegistrar.getSecureRandom();
        }
        if (this.key instanceof RSAPrivateCrtKeyParameters) {
            throw new IllegalArgumentException("generator requires RSA public key");
        }
    }

    public BigInteger generateBlindingFactor() {
        BigInteger gcd;
        BigInteger factor;
        if (this.key == null) {
            throw new IllegalStateException("generator not initialised");
        }
        BigInteger m = this.key.getModulus();
        int length = m.bitLength() - 1;
        do {
            factor = BigIntegers.createRandomBigInteger(length, this.random);
            gcd = factor.gcd(m);
        } while (factor.equals(ZERO) || factor.equals(ONE) || !gcd.equals(ONE));
        return factor;
    }
}

