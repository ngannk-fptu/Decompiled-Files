/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.security;

public class AuthorisationException
extends SecurityException {
    public AuthorisationException() {
        this("Client must be authenticated as a system administrator to access this resource.");
    }

    public AuthorisationException(String message) {
        super(message);
    }
}

