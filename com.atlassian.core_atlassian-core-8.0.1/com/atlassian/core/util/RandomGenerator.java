/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util;

public class RandomGenerator {
    public static String randomPassword() {
        return RandomGenerator.randomString(6);
    }

    public static String randomString(int length) {
        return RandomGenerator.randomString(length, true);
    }

    public static String randomString(int length, boolean includeNumbers) {
        StringBuilder b = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            if (includeNumbers) {
                b.append(RandomGenerator.randomCharacter());
                continue;
            }
            b.append(RandomGenerator.randomAlpha());
        }
        return b.toString();
    }

    public static char randomCharacter() {
        int i = (int)(Math.random() * 3.0);
        if (i < 2) {
            return RandomGenerator.randomAlpha();
        }
        return RandomGenerator.randomDigit();
    }

    public static char randomAlpha() {
        int i = (int)(Math.random() * 52.0);
        if (i > 25) {
            return (char)(97 + i - 26);
        }
        return (char)(65 + i);
    }

    public static char randomDigit() {
        int i = (int)(Math.random() * 10.0);
        return (char)(48 + i);
    }
}

