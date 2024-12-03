/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.security;

public class AuthenticationException
extends SecurityException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

