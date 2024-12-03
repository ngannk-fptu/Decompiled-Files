/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.userdetails;

import org.springframework.security.core.AuthenticationException;

public class UsernameNotFoundException
extends AuthenticationException {
    public UsernameNotFoundException(String msg) {
        super(msg);
    }

    public UsernameNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

