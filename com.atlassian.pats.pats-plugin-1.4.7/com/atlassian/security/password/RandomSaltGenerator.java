/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.password;

import com.atlassian.security.password.SaltGenerator;
import java.util.Random;

public final class RandomSaltGenerator
implements SaltGenerator {
    private static final Random random = new Random();

    @Override
    public byte[] generateSalt(int length) {
        byte[] result = new byte[length];
        random.nextBytes(result);
        return result;
    }
}

