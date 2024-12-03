/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang3;

import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.Validate;

@Deprecated
public class RandomUtils {
    public static boolean nextBoolean() {
        return RandomUtils.random().nextBoolean();
    }

    public static byte[] nextBytes(int count) {
        Validate.isTrue(count >= 0, "Count cannot be negative.", new Object[0]);
        byte[] result = new byte[count];
        RandomUtils.random().nextBytes(result);
        return result;
    }

    public static double nextDouble() {
        return RandomUtils.nextDouble(0.0, Double.MAX_VALUE);
    }

    public static double nextDouble(double startInclusive, double endExclusive) {
        Validate.isTrue(endExclusive >= startInclusive, "Start value must be smaller or equal to end value.", new Object[0]);
        Validate.isTrue(startInclusive >= 0.0, "Both range values must be non-negative.", new Object[0]);
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + (endExclusive - startInclusive) * RandomUtils.random().nextDouble();
    }

    public static float nextFloat() {
        return RandomUtils.nextFloat(0.0f, Float.MAX_VALUE);
    }

    public static float nextFloat(float startInclusive, float endExclusive) {
        Validate.isTrue(endExclusive >= startInclusive, "Start value must be smaller or equal to end value.", new Object[0]);
        Validate.isTrue(startInclusive >= 0.0f, "Both range values must be non-negative.", new Object[0]);
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + (endExclusive - startInclusive) * RandomUtils.random().nextFloat();
    }

    public static int nextInt() {
        return RandomUtils.nextInt(0, Integer.MAX_VALUE);
    }

    public static int nextInt(int startInclusive, int endExclusive) {
        Validate.isTrue(endExclusive >= startInclusive, "Start value must be smaller or equal to end value.", new Object[0]);
        Validate.isTrue(startInclusive >= 0, "Both range values must be non-negative.", new Object[0]);
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + RandomUtils.random().nextInt(endExclusive - startInclusive);
    }

    public static long nextLong() {
        return RandomUtils.nextLong(Long.MAX_VALUE);
    }

    private static long nextLong(long n) {
        long val;
        long bits;
        while ((bits = RandomUtils.random().nextLong() >>> 1) - (val = bits % n) + (n - 1L) < 0L) {
        }
        return val;
    }

    public static long nextLong(long startInclusive, long endExclusive) {
        Validate.isTrue(endExclusive >= startInclusive, "Start value must be smaller or equal to end value.", new Object[0]);
        Validate.isTrue(startInclusive >= 0L, "Both range values must be non-negative.", new Object[0]);
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + RandomUtils.nextLong(endExclusive - startInclusive);
    }

    private static ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }
}

