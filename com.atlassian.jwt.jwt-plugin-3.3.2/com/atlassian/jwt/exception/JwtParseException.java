/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.exception;

public class JwtParseException
extends Exception {
    public JwtParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtParseException(Exception cause) {
        super(cause);
    }

    public JwtParseException(String reason) {
        super(reason);
    }
}

