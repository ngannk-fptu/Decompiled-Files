/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access.intercept;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@Deprecated
public class RunAsUserToken
extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 580L;
    private final Class<? extends Authentication> originalAuthentication;
    private final Object credentials;
    private final Object principal;
    private final int keyHash;

    public RunAsUserToken(String key, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, Class<? extends Authentication> originalAuthentication) {
        super(authorities);
        this.keyHash = key.hashCode();
        this.principal = principal;
        this.credentials = credentials;
        this.originalAuthentication = originalAuthentication;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    public int getKeyHash() {
        return this.keyHash;
    }

    public Class<? extends Authentication> getOriginalAuthentication() {
        return this.originalAuthentication;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        String className = this.originalAuthentication != null ? this.originalAuthentication.getName() : null;
        sb.append("; Original Class: ").append(className);
        return sb.toString();
    }
}

