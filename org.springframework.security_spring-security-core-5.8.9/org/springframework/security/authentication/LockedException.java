/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication;

import org.springframework.security.authentication.AccountStatusException;

public class LockedException
extends AccountStatusException {
    public LockedException(String msg) {
        super(msg);
    }

    public LockedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

