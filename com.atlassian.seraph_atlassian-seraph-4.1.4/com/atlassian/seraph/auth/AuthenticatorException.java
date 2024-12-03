/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.auth;

import com.atlassian.seraph.auth.AuthenticationErrorType;

public class AuthenticatorException
extends Exception {
    private AuthenticationErrorType errorType;

    public AuthenticatorException() {
    }

    public AuthenticatorException(String s) {
        super(s);
    }

    public AuthenticatorException(AuthenticationErrorType errorType) {
        this(errorType.name());
        this.errorType = errorType;
    }

    public AuthenticationErrorType getErrorType() {
        return this.errorType;
    }
}

