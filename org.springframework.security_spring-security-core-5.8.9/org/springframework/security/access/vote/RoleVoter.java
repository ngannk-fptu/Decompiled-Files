/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access.vote;

import java.util.Collection;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@Deprecated
public class RoleVoter
implements AccessDecisionVoter<Object> {
    private String rolePrefix = "ROLE_";

    public String getRolePrefix() {
        return this.rolePrefix;
    }

    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute.getAttribute() != null && attribute.getAttribute().startsWith(this.getRolePrefix());
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        if (authentication == null) {
            return -1;
        }
        int result = 0;
        Collection<? extends GrantedAuthority> authorities = this.extractAuthorities(authentication);
        for (ConfigAttribute attribute : attributes) {
            if (!this.supports(attribute)) continue;
            result = -1;
            for (GrantedAuthority grantedAuthority : authorities) {
                if (!attribute.getAttribute().equals(grantedAuthority.getAuthority())) continue;
                return 1;
            }
        }
        return result;
    }

    Collection<? extends GrantedAuthority> extractAuthorities(Authentication authentication) {
        return authentication.getAuthorities();
    }
}

