/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.password.SaltGenerator
 */
package com.atlassian.crowd.password.saltgenerator;

import com.atlassian.security.password.SaltGenerator;
import java.security.SecureRandom;

public class SecureRandomSaltGenerator
implements SaltGenerator {
    public static final SecureRandomSaltGenerator INSTANCE = new SecureRandomSaltGenerator();
    private final SecureRandom SECURE_RANDOM = new SecureRandom();

    public byte[] generateSalt(int length) {
        byte[] result = new byte[length];
        this.SECURE_RANDOM.nextBytes(result);
        return result;
    }
}

