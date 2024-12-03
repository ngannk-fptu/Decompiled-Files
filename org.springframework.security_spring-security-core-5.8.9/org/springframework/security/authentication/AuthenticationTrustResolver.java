/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication;

import org.springframework.security.core.Authentication;

public interface AuthenticationTrustResolver {
    public boolean isAnonymous(Authentication var1);

    public boolean isRememberMe(Authentication var1);
}

