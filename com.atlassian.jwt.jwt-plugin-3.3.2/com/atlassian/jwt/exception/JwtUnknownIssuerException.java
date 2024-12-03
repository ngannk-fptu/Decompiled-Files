/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.exception;

public class JwtUnknownIssuerException
extends Exception {
    public JwtUnknownIssuerException(String issuer) {
        super(issuer);
    }
}

