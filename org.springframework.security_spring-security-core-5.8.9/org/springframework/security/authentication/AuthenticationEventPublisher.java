/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public interface AuthenticationEventPublisher {
    public void publishAuthenticationSuccess(Authentication var1);

    public void publishAuthenticationFailure(AuthenticationException var1, Authentication var2);
}

