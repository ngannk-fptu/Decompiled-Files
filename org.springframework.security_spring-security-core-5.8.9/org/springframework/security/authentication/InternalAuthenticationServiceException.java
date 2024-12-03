/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication;

import org.springframework.security.authentication.AuthenticationServiceException;

public class InternalAuthenticationServiceException
extends AuthenticationServiceException {
    public InternalAuthenticationServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalAuthenticationServiceException(String message) {
        super(message);
    }
}

