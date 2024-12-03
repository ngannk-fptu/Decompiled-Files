/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.random.DefaultSecureRandomService
 *  com.atlassian.security.random.SecureRandomService
 */
package com.atlassian.crowd.util;

import com.atlassian.security.random.DefaultSecureRandomService;
import com.atlassian.security.random.SecureRandomService;

public class SecureRandomStringUtils {
    private static SecureRandomStringUtils INSTANCE = new SecureRandomStringUtils(DefaultSecureRandomService.getInstance());
    private static final char[] ALPHANUMERICS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private final SecureRandomService random;

    SecureRandomStringUtils(SecureRandomService random) {
        this.random = random;
    }

    public static SecureRandomStringUtils getInstance() {
        return INSTANCE;
    }

    public String randomString(int length, char[] alphabet) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        if (alphabet.length <= 0) {
            throw new IllegalArgumentException("Alphabet must contain at least one character");
        }
        char[] buffer = new char[length];
        for (int i = 0; i < length; ++i) {
            buffer[i] = alphabet[this.random.nextInt(alphabet.length)];
        }
        return new String(buffer);
    }

    public String randomAlphanumericString(int length) {
        return this.randomString(length, ALPHANUMERICS);
    }
}

