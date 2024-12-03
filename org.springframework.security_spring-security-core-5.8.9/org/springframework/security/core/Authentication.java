/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public interface Authentication
extends Principal,
Serializable {
    public Collection<? extends GrantedAuthority> getAuthorities();

    public Object getCredentials();

    public Object getDetails();

    public Object getPrincipal();

    public boolean isAuthenticated();

    public void setAuthenticated(boolean var1) throws IllegalArgumentException;
}

