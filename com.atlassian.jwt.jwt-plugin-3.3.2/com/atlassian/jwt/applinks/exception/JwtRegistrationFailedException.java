/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.applinks.exception;

public class JwtRegistrationFailedException
extends Exception {
    public JwtRegistrationFailedException(String message) {
        super(message);
    }

    public JwtRegistrationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtRegistrationFailedException(Throwable cause) {
        super(cause);
    }
}

