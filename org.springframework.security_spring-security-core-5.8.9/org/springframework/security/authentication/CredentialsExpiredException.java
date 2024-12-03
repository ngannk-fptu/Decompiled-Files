/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication;

import org.springframework.security.authentication.AccountStatusException;

public class CredentialsExpiredException
extends AccountStatusException {
    public CredentialsExpiredException(String msg) {
        super(msg);
    }

    public CredentialsExpiredException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

