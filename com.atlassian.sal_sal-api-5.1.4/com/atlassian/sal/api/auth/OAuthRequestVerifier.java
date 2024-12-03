/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.auth;

public interface OAuthRequestVerifier {
    public boolean isVerified();

    public void setVerified(boolean var1);

    public void clear();
}

