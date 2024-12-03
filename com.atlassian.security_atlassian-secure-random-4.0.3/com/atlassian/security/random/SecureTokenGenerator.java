/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.random;

public interface SecureTokenGenerator {
    public String generateToken();

    public String generateNonce();
}

