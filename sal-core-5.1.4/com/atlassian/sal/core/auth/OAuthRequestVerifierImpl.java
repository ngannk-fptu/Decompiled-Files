/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.OAuthRequestVerifier
 */
package com.atlassian.sal.core.auth;

import com.atlassian.sal.api.auth.OAuthRequestVerifier;

public class OAuthRequestVerifierImpl
implements OAuthRequestVerifier {
    private static final ThreadLocal<Boolean> isVerified = new ThreadLocal();

    public boolean isVerified() {
        return Boolean.TRUE.equals(isVerified.get());
    }

    public void setVerified(boolean val) {
        isVerified.set(val);
    }

    public void clear() {
        isVerified.remove();
    }
}

