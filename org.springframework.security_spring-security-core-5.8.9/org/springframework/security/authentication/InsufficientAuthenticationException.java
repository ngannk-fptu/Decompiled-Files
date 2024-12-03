/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication;

import org.springframework.security.core.AuthenticationException;

public class InsufficientAuthenticationException
extends AuthenticationException {
    public InsufficientAuthenticationException(String msg) {
        super(msg);
    }

    public InsufficientAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

