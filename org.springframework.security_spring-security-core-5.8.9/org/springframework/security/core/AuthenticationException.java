/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core;

public abstract class AuthenticationException
extends RuntimeException {
    public AuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthenticationException(String msg) {
        super(msg);
    }
}

