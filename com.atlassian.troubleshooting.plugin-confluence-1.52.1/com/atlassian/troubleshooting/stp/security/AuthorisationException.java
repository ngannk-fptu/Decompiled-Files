/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.security;

public class AuthorisationException
extends SecurityException {
    public AuthorisationException(String message) {
        super(message);
    }

    public AuthorisationException(String message, Throwable cause) {
        super(message, cause);
    }
}

