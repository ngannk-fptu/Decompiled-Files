/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.random;

import com.atlassian.security.random.SecureRandomFactory;
import com.atlassian.security.random.SecureRandomService;
import java.security.SecureRandom;

public final class DefaultSecureRandomService
implements SecureRandomService {
    private static final SecureRandomService INSTANCE = new DefaultSecureRandomService(SecureRandomFactory.newInstance());
    private final SecureRandom random;

    DefaultSecureRandomService(SecureRandom random) {
        this.random = random;
    }

    public static SecureRandomService getInstance() {
        return INSTANCE;
    }

    @Override
    public void nextBytes(byte[] bytes) {
        this.random.nextBytes(bytes);
    }

    @Override
    public int nextInt() {
        return this.random.nextInt();
    }

    @Override
    public int nextInt(int n) {
        return this.random.nextInt(n);
    }

    @Override
    public long nextLong() {
        return this.random.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return this.random.nextBoolean();
    }

    @Override
    public float nextFloat() {
        return this.random.nextFloat();
    }

    @Override
    public double nextDouble() {
        return this.random.nextDouble();
    }
}

