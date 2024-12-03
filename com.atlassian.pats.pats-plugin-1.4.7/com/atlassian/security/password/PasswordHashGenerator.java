/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.password;

public interface PasswordHashGenerator {
    public byte[] generateHash(byte[] var1, byte[] var2);

    public int getRequiredSaltLength();
}

