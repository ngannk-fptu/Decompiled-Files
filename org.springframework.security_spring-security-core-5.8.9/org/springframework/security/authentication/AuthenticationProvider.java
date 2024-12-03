/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public interface AuthenticationProvider {
    public Authentication authenticate(Authentication var1) throws AuthenticationException;

    public boolean supports(Class<?> var1);
}

