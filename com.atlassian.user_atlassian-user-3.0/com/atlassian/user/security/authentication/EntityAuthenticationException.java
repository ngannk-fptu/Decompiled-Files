/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.security.authentication;

import com.atlassian.user.EntityException;

public class EntityAuthenticationException
extends EntityException {
    public EntityAuthenticationException() {
    }

    public EntityAuthenticationException(String message) {
        super(message);
    }

    public EntityAuthenticationException(Throwable cause) {
        super(cause);
    }

    public EntityAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

