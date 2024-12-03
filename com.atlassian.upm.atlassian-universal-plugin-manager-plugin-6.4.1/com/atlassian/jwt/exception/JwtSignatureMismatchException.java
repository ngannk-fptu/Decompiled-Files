/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.exception;

import com.atlassian.jwt.exception.JwtVerificationException;

public class JwtSignatureMismatchException
extends JwtVerificationException {
    private String issuer;

    public JwtSignatureMismatchException(Exception cause) {
        super(cause);
    }

    @Deprecated
    public JwtSignatureMismatchException(String reason) {
        super(reason);
    }

    public JwtSignatureMismatchException(String reason, String issuer) {
        super(reason);
        this.issuer = issuer;
    }

    public String getIssuer() {
        return this.issuer;
    }
}

