/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import java.util.Random;

public final class RandomPicker {
    private static Random randomNumberGenerator;

    private RandomPicker() {
    }

    private static synchronized void initRNG() {
        if (randomNumberGenerator == null) {
            randomNumberGenerator = new Random();
        }
    }

    public static int getInt(int n) {
        if (randomNumberGenerator == null) {
            RandomPicker.initRNG();
        }
        return randomNumberGenerator.nextInt(n);
    }

    public static int getInt(int low, int high) {
        return RandomPicker.getInt(high - low) + low;
    }
}

