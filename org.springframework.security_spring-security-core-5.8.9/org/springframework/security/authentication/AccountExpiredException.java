/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication;

import org.springframework.security.authentication.AccountStatusException;

public class AccountExpiredException
extends AccountStatusException {
    public AccountExpiredException(String msg) {
        super(msg);
    }

    public AccountExpiredException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

