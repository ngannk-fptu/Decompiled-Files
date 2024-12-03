/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.exception;

public abstract class JwtVerificationException
extends Exception {
    protected JwtVerificationException() {
    }

    protected JwtVerificationException(String message) {
        super(message);
    }

    protected JwtVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

    protected JwtVerificationException(Throwable cause) {
        super(cause);
    }
}

