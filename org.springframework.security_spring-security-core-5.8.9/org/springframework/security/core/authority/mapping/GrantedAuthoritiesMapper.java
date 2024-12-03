/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.authority.mapping;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public interface GrantedAuthoritiesMapper {
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> var1);
}

