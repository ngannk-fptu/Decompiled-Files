/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.client.lib.web;

public class AuthenticationFailedException
extends RuntimeException {
    public AuthenticationFailedException(String message) {
        super(message);
    }

    public AuthenticationFailedException(Throwable cause) {
        super(cause);
    }

    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

