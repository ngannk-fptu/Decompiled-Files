/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

public class InvalidTokenException
extends Exception {
    public InvalidTokenException() {
    }

    public InvalidTokenException(String s) {
        super(s);
    }

    public InvalidTokenException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InvalidTokenException(Throwable throwable) {
        super(throwable);
    }
}

