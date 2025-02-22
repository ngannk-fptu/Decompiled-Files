/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

import java.security.SecureRandom;
import java.util.Random;

public final class ThreadLocalRandomProvider {
    private static final Random SEED_GENERATOR = new Random();
    private static final ThreadLocal<Random> THREAD_LOCAL_RANDOM = new ThreadLocal();
    private static final ThreadLocal<SecureRandom> THREAD_LOCAL_SECURE_RANDOM = new ThreadLocal();

    private ThreadLocalRandomProvider() {
    }

    public static Random get() {
        Random random = THREAD_LOCAL_RANDOM.get();
        if (random == null) {
            long seed = SEED_GENERATOR.nextLong();
            random = new Random(seed);
            THREAD_LOCAL_RANDOM.set(random);
        }
        return random;
    }

    public static SecureRandom getSecure() {
        SecureRandom random = THREAD_LOCAL_SECURE_RANDOM.get();
        if (random == null) {
            random = new SecureRandom();
            THREAD_LOCAL_SECURE_RANDOM.set(random);
        }
        return random;
    }
}

