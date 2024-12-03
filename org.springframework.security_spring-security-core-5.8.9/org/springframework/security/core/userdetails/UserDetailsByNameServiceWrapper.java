/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 */
package org.springframework.security.core.userdetails;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class UserDetailsByNameServiceWrapper<T extends Authentication>
implements AuthenticationUserDetailsService<T>,
InitializingBean {
    private UserDetailsService userDetailsService = null;

    public UserDetailsByNameServiceWrapper() {
    }

    public UserDetailsByNameServiceWrapper(UserDetailsService userDetailsService) {
        Assert.notNull((Object)userDetailsService, (String)"userDetailsService cannot be null.");
        this.userDetailsService = userDetailsService;
    }

    public void afterPropertiesSet() {
        Assert.notNull((Object)this.userDetailsService, (String)"UserDetailsService must be set");
    }

    @Override
    public UserDetails loadUserDetails(T authentication) throws UsernameNotFoundException {
        return this.userDetailsService.loadUserByUsername(authentication.getName());
    }

    public void setUserDetailsService(UserDetailsService aUserDetailsService) {
        this.userDetailsService = aUserDetailsService;
    }
}

