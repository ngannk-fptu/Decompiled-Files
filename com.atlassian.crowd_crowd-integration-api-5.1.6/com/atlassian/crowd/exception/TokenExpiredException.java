/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.InvalidTokenException;

public class TokenExpiredException
extends InvalidTokenException {
    public TokenExpiredException() {
    }

    public TokenExpiredException(String msg) {
        super(msg);
    }

    public TokenExpiredException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public TokenExpiredException(Throwable throwable) {
        super(throwable);
    }
}

