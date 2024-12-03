/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.BigIntegers;

class DHKeyGeneratorHelper {
    static final DHKeyGeneratorHelper INSTANCE = new DHKeyGeneratorHelper();
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private static final BigInteger TWO = BigInteger.valueOf(2L);

    private DHKeyGeneratorHelper() {
    }

    BigInteger calculatePrivate(DHParameters dhParams, SecureRandom random) {
        BigInteger x;
        BigInteger q;
        int limit = dhParams.getL();
        if (limit != 0) {
            BigInteger x2;
            int minWeight = limit >>> 2;
            while (WNafUtil.getNafWeight(x2 = BigIntegers.createRandomBigInteger(limit, random).setBit(limit - 1)) < minWeight) {
            }
            return x2;
        }
        BigInteger min = TWO;
        int m = dhParams.getM();
        if (m != 0) {
            min = ONE.shiftLeft(m - 1);
        }
        if ((q = dhParams.getQ()) == null) {
            q = dhParams.getP();
        }
        BigInteger max = q.subtract(TWO);
        int minWeight = max.bitLength() >>> 2;
        while (WNafUtil.getNafWeight(x = BigIntegers.createRandomInRange(min, max, random)) < minWeight) {
        }
        return x;
    }

    BigInteger calculatePublic(DHParameters dhParams, BigInteger x) {
        return dhParams.getG().modPow(x, dhParams.getP());
    }
}

