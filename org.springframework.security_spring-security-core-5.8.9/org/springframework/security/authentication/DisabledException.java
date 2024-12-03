/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication;

import org.springframework.security.authentication.AccountStatusException;

public class DisabledException
extends AccountStatusException {
    public DisabledException(String msg) {
        super(msg);
    }

    public DisabledException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

