/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authorization;

import java.util.Collection;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.GrantedAuthority;

public class AuthorityAuthorizationDecision
extends AuthorizationDecision {
    private final Collection<GrantedAuthority> authorities;

    public AuthorityAuthorizationDecision(boolean granted, Collection<GrantedAuthority> authorities) {
        super(granted);
        this.authorities = authorities;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [granted=" + this.isGranted() + ", authorities=" + this.authorities + ']';
    }
}

