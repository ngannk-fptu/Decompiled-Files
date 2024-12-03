/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.vote;

import java.util.Collection;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

@Deprecated
public class RoleHierarchyVoter
extends RoleVoter {
    private RoleHierarchy roleHierarchy = null;

    public RoleHierarchyVoter(RoleHierarchy roleHierarchy) {
        Assert.notNull((Object)roleHierarchy, (String)"RoleHierarchy must not be null");
        this.roleHierarchy = roleHierarchy;
    }

    @Override
    Collection<? extends GrantedAuthority> extractAuthorities(Authentication authentication) {
        return this.roleHierarchy.getReachableGrantedAuthorities(authentication.getAuthorities());
    }
}

