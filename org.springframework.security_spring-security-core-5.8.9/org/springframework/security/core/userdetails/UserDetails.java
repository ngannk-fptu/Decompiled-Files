/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.userdetails;

import java.io.Serializable;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public interface UserDetails
extends Serializable {
    public Collection<? extends GrantedAuthority> getAuthorities();

    public String getPassword();

    public String getUsername();

    public boolean isAccountNonExpired();

    public boolean isAccountNonLocked();

    public boolean isCredentialsNonExpired();

    public boolean isEnabled();
}

