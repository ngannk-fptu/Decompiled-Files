/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.trust;

import com.atlassian.confluence.security.trust.TrustedToken;

public interface TrustedTokenFactory {
    public TrustedToken getToken(String var1);
}

