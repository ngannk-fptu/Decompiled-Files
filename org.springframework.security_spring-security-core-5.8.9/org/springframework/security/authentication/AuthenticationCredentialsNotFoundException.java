/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationCredentialsNotFoundException
extends AuthenticationException {
    public AuthenticationCredentialsNotFoundException(String msg) {
        super(msg);
    }

    public AuthenticationCredentialsNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

