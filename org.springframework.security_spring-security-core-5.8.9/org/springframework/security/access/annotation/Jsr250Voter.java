/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access.annotation;

import java.util.Collection;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.annotation.Jsr250SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@Deprecated
public class Jsr250Voter
implements AccessDecisionVoter<Object> {
    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return configAttribute instanceof Jsr250SecurityConfig;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> definition) {
        boolean jsr250AttributeFound = false;
        for (ConfigAttribute attribute : definition) {
            if (Jsr250SecurityConfig.PERMIT_ALL_ATTRIBUTE.equals(attribute)) {
                return 1;
            }
            if (Jsr250SecurityConfig.DENY_ALL_ATTRIBUTE.equals(attribute)) {
                return -1;
            }
            if (!this.supports(attribute)) continue;
            jsr250AttributeFound = true;
            for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
                if (!attribute.getAttribute().equals(grantedAuthority.getAuthority())) continue;
                return 1;
            }
        }
        return jsr250AttributeFound ? -1 : 0;
    }
}

