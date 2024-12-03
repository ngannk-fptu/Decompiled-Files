/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.ec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.util.BigIntegers;

class ECUtil {
    ECUtil() {
    }

    static BigInteger generateK(BigInteger n, SecureRandom random) {
        BigInteger k;
        int nBitLength = n.bitLength();
        while ((k = BigIntegers.createRandomBigInteger(nBitLength, random)).equals(ECConstants.ZERO) || k.compareTo(n) >= 0) {
        }
        return k;
    }
}

