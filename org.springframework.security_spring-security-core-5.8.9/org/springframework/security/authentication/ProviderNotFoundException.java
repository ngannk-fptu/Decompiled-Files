/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication;

import org.springframework.security.core.AuthenticationException;

public class ProviderNotFoundException
extends AuthenticationException {
    public ProviderNotFoundException(String msg) {
        super(msg);
    }
}

