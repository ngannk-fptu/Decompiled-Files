/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.security.authentication;

import com.atlassian.user.EntityException;

public class InvalidPasswordException
extends EntityException {
    public InvalidPasswordException() {
    }

    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(Throwable cause) {
        super(cause);
    }

    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}

