/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.permission;

import com.atlassian.sal.api.permission.AuthorisationException;

public class NotAuthenticatedException
extends AuthorisationException {
    public NotAuthenticatedException() {
    }

    public NotAuthenticatedException(String message) {
        super(message);
    }

    public NotAuthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAuthenticatedException(Throwable cause) {
        super(cause);
    }
}

