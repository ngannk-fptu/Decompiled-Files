/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import org.springframework.util.IdGenerator;

public class AlternativeJdkIdGenerator
implements IdGenerator {
    private final Random random;

    public AlternativeJdkIdGenerator() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] seed = new byte[8];
        secureRandom.nextBytes(seed);
        this.random = new Random(new BigInteger(seed).longValue());
    }

    @Override
    public UUID generateId() {
        byte[] randomBytes = new byte[16];
        this.random.nextBytes(randomBytes);
        long mostSigBits = 0L;
        for (int i2 = 0; i2 < 8; ++i2) {
            mostSigBits = mostSigBits << 8 | (long)(randomBytes[i2] & 0xFF);
        }
        long leastSigBits = 0L;
        for (int i3 = 8; i3 < 16; ++i3) {
            leastSigBits = leastSigBits << 8 | (long)(randomBytes[i3] & 0xFF);
        }
        return new UUID(mostSigBits, leastSigBits);
    }
}

