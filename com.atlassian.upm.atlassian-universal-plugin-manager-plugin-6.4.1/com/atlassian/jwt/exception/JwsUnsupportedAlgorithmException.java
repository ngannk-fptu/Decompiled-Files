/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.exception;

import com.atlassian.jwt.exception.JwtVerificationException;

public class JwsUnsupportedAlgorithmException
extends JwtVerificationException {
    public JwsUnsupportedAlgorithmException(String message) {
        super(message);
    }

    public JwsUnsupportedAlgorithmException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwsUnsupportedAlgorithmException(Throwable cause) {
        super(cause);
    }
}

