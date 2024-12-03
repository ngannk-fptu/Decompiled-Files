/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.exception;

public class JwtIssuerLacksSharedSecretException
extends Exception {
    public JwtIssuerLacksSharedSecretException(String issuer) {
        super(issuer);
    }
}

