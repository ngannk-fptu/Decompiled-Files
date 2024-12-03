/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access.hierarchicalroles;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public interface RoleHierarchy {
    public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(Collection<? extends GrantedAuthority> var1);
}

