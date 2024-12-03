/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.trust;

public interface TrustedToken {
    public String getUserName();

    public String getApplicationId();

    public String getEncodedToken();

    public String getEncodedKey();

    public String getMagicNumber();

    public Integer getProtocolVersion();

    public String getSignature();
}

