/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication;

import org.springframework.security.core.AuthenticationException;

public abstract class AccountStatusException
extends AuthenticationException {
    public AccountStatusException(String msg) {
        super(msg);
    }

    public AccountStatusException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

