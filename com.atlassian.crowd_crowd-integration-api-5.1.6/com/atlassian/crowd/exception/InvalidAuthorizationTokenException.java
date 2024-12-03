/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

public class InvalidAuthorizationTokenException
extends Exception {
    public InvalidAuthorizationTokenException() {
    }

    public InvalidAuthorizationTokenException(String s) {
        super(s);
    }

    public InvalidAuthorizationTokenException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InvalidAuthorizationTokenException(Throwable throwable) {
        super(throwable);
    }
}

