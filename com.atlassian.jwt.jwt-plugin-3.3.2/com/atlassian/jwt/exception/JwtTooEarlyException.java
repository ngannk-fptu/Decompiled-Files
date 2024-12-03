/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.exception;

import com.atlassian.jwt.exception.JwtVerificationException;
import java.util.Date;

public class JwtTooEarlyException
extends JwtVerificationException {
    public JwtTooEarlyException(Date notBefore, Date now, int leewaySeconds) {
        super(String.format("Not-before time is %s and time is now %s (%d leeway seconds is allowed)", notBefore, now, leewaySeconds));
    }
}

