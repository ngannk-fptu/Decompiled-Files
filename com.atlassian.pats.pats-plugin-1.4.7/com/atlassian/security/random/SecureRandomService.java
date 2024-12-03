/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.random;

public interface SecureRandomService {
    public void nextBytes(byte[] var1);

    public int nextInt();

    public int nextInt(int var1);

    public long nextLong();

    public boolean nextBoolean();

    public float nextFloat();

    public double nextDouble();
}

