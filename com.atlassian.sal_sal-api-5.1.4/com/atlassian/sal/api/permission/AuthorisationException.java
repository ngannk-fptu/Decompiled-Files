/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.permission;

public class AuthorisationException
extends RuntimeException {
    public AuthorisationException() {
    }

    public AuthorisationException(String message) {
        super(message);
    }

    public AuthorisationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorisationException(Throwable cause) {
        super(cause);
    }
}

