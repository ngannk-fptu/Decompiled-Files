/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.intercept;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

@Deprecated
public class RunAsManagerImpl
implements RunAsManager,
InitializingBean {
    private String key;
    private String rolePrefix = "ROLE_";

    public void afterPropertiesSet() {
        Assert.notNull((Object)this.key, (String)"A Key is required and should match that configured for the RunAsImplAuthenticationProvider");
    }

    @Override
    public Authentication buildRunAs(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        ArrayList<? extends GrantedAuthority> newAuthorities = new ArrayList<GrantedAuthority>();
        for (ConfigAttribute attribute : attributes) {
            if (!this.supports(attribute)) continue;
            SimpleGrantedAuthority extraAuthority = new SimpleGrantedAuthority(this.getRolePrefix() + attribute.getAttribute());
            newAuthorities.add(extraAuthority);
        }
        if (newAuthorities.size() == 0) {
            return null;
        }
        newAuthorities.addAll(authentication.getAuthorities());
        return new RunAsUserToken(this.key, authentication.getPrincipal(), authentication.getCredentials(), newAuthorities, authentication.getClass());
    }

    public String getKey() {
        return this.key;
    }

    public String getRolePrefix() {
        return this.rolePrefix;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute.getAttribute() != null && attribute.getAttribute().startsWith("RUN_AS_");
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}

