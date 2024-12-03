/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.exception;

import com.atlassian.jwt.exception.JwtParseException;

public class JwtMissingClaimException
extends JwtParseException {
    public JwtMissingClaimException(String reason) {
        super(reason);
    }
}

