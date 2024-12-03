/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.InvalidTokenException;

public class TokenNotFoundException
extends InvalidTokenException {
    public TokenNotFoundException() {
    }

    public TokenNotFoundException(String s) {
        super(s);
    }

    public TokenNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public TokenNotFoundException(Throwable throwable) {
        super(throwable);
    }
}

