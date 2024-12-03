/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.authority;

import java.io.Serializable;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public interface GrantedAuthoritiesContainer
extends Serializable {
    public Collection<? extends GrantedAuthority> getGrantedAuthorities();
}

