/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.exception;

import com.atlassian.jwt.exception.JwtVerificationException;
import java.util.Date;

public class JwtExpiredException
extends JwtVerificationException {
    public JwtExpiredException(Date expiredAt, Date now, int leewaySeconds) {
        super(String.format("Expired at %s and time is now %s (%d seconds leeway is allowed)", expiredAt, now, leewaySeconds));
    }
}

