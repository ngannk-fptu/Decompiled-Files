/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.exception;

import com.atlassian.jwt.exception.JwtVerificationException;

public class JwtInvalidClaimException
extends JwtVerificationException {
    public JwtInvalidClaimException(String message) {
        super(message);
    }

    public JwtInvalidClaimException(String message, Throwable cause) {
        super(message, cause);
    }
}

