/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.authority.mapping;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public interface Attributes2GrantedAuthoritiesMapper {
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(Collection<String> var1);
}

