/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access.hierarchicalroles;

import java.util.Collection;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;

public final class NullRoleHierarchy
implements RoleHierarchy {
    @Override
    public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities;
    }
}

