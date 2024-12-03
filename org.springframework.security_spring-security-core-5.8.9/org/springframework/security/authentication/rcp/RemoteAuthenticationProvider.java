/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 */
package org.springframework.security.authentication.rcp;

import java.util.Collection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.rcp.RemoteAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

@Deprecated
public class RemoteAuthenticationProvider
implements AuthenticationProvider,
InitializingBean {
    private RemoteAuthenticationManager remoteAuthenticationManager;

    public void afterPropertiesSet() {
        Assert.notNull((Object)this.remoteAuthenticationManager, (String)"remoteAuthenticationManager is mandatory");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal().toString();
        Object credentials = authentication.getCredentials();
        String password = credentials != null ? credentials.toString() : null;
        Collection<? extends GrantedAuthority> authorities = this.remoteAuthenticationManager.attemptAuthentication(username, password);
        return UsernamePasswordAuthenticationToken.authenticated(username, password, authorities);
    }

    public RemoteAuthenticationManager getRemoteAuthenticationManager() {
        return this.remoteAuthenticationManager;
    }

    public void setRemoteAuthenticationManager(RemoteAuthenticationManager remoteAuthenticationManager) {
        this.remoteAuthenticationManager = remoteAuthenticationManager;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

