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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.rcp.RemoteAuthenticationException;
import org.springframework.security.authentication.rcp.RemoteAuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

@Deprecated
public class RemoteAuthenticationManagerImpl
implements RemoteAuthenticationManager,
InitializingBean {
    private AuthenticationManager authenticationManager;

    public void afterPropertiesSet() {
        Assert.notNull((Object)this.authenticationManager, (String)"authenticationManager is required");
    }

    @Override
    public Collection<? extends GrantedAuthority> attemptAuthentication(String username, String password) throws RemoteAuthenticationException {
        UsernamePasswordAuthenticationToken request = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        try {
            return this.authenticationManager.authenticate(request).getAuthorities();
        }
        catch (AuthenticationException ex) {
            throw new RemoteAuthenticationException(ex.getMessage());
        }
    }

    protected AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}

