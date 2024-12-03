/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication.jaas;

import javax.security.auth.login.LoginException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.jaas.LoginExceptionResolver;
import org.springframework.security.core.AuthenticationException;

public class DefaultLoginExceptionResolver
implements LoginExceptionResolver {
    @Override
    public AuthenticationException resolveException(LoginException ex) {
        return new AuthenticationServiceException(ex.getMessage(), ex);
    }
}

