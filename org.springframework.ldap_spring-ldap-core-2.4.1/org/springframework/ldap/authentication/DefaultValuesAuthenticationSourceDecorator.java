/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.StringUtils
 */
package org.springframework.ldap.authentication;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.util.StringUtils;

public class DefaultValuesAuthenticationSourceDecorator
implements AuthenticationSource,
InitializingBean {
    private AuthenticationSource target;
    private String defaultUser;
    private String defaultPassword;

    public DefaultValuesAuthenticationSourceDecorator() {
    }

    public DefaultValuesAuthenticationSourceDecorator(AuthenticationSource target, String defaultUser, String defaultPassword) {
        this.target = target;
        this.defaultUser = defaultUser;
        this.defaultPassword = defaultPassword;
    }

    @Override
    public String getCredentials() {
        if (StringUtils.hasText((String)this.target.getPrincipal())) {
            return this.target.getCredentials();
        }
        return this.defaultPassword;
    }

    @Override
    public String getPrincipal() {
        String principal = this.target.getPrincipal();
        if (StringUtils.hasText((String)principal)) {
            return principal;
        }
        return this.defaultUser;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public void setDefaultUser(String defaultUser) {
        this.defaultUser = defaultUser;
    }

    public void setTarget(AuthenticationSource target) {
        this.target = target;
    }

    public void afterPropertiesSet() throws Exception {
        if (this.target == null) {
            throw new IllegalArgumentException("Property 'target' must be set.'");
        }
        if (this.defaultUser == null) {
            throw new IllegalArgumentException("Property 'defaultUser' must be set.'");
        }
        if (this.defaultPassword == null) {
            throw new IllegalArgumentException("Property 'defaultPassword' must be set.'");
        }
    }
}

