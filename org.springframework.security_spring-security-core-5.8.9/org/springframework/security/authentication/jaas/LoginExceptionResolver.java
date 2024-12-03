/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication.jaas;

import javax.security.auth.login.LoginException;
import org.springframework.security.core.AuthenticationException;

public interface LoginExceptionResolver {
    public AuthenticationException resolveException(LoginException var1);
}

