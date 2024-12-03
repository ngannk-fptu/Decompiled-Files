/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

@Deprecated
public interface EncryptedCertificate {
    public String getID();

    public String getSecretKey();

    public String getCertificate();

    public Integer getProtocolVersion();

    public String getMagicNumber();

    public String getSignature();
}

