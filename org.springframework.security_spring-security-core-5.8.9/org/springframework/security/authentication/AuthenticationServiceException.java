/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationServiceException
extends AuthenticationException {
    public AuthenticationServiceException(String msg) {
        super(msg);
    }

    public AuthenticationServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

