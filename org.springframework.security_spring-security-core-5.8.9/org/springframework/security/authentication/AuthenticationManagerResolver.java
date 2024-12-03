/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication;

import org.springframework.security.authentication.AuthenticationManager;

public interface AuthenticationManagerResolver<C> {
    public AuthenticationManager resolve(C var1);
}

