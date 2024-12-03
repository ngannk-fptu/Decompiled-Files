/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.math;

import java.util.Random;
import org.apache.commons.lang.math.JVMRandom;

public class RandomUtils {
    public static final Random JVM_RANDOM = new JVMRandom();

    public static int nextInt() {
        return RandomUtils.nextInt(JVM_RANDOM);
    }

    public static int nextInt(Random random) {
        return random.nextInt();
    }

    public static int nextInt(int n) {
        return RandomUtils.nextInt(JVM_RANDOM, n);
    }

    public static int nextInt(Random random, int n) {
        return random.nextInt(n);
    }

    public static long nextLong() {
        return RandomUtils.nextLong(JVM_RANDOM);
    }

    public static long nextLong(Random random) {
        return random.nextLong();
    }

    public static boolean nextBoolean() {
        return RandomUtils.nextBoolean(JVM_RANDOM);
    }

    public static boolean nextBoolean(Random random) {
        return random.nextBoolean();
    }

    public static float nextFloat() {
        return RandomUtils.nextFloat(JVM_RANDOM);
    }

    public static float nextFloat(Random random) {
        return random.nextFloat();
    }

    public static double nextDouble() {
        return RandomUtils.nextDouble(JVM_RANDOM);
    }

    public static double nextDouble(Random random) {
        return random.nextDouble();
    }
}

