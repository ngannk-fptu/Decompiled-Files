/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user;

public class UserManagerImplementationException
extends RuntimeException {
    private Throwable cause;

    public UserManagerImplementationException() {
    }

    public UserManagerImplementationException(String s) {
        super(s);
    }

    public UserManagerImplementationException(Throwable cause) {
        this.cause = cause;
    }

    public UserManagerImplementationException(String s, Throwable cause) {
        super(s);
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }
}

