/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.authentication;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

public abstract class AbstractAuthenticationToken
implements Authentication,
CredentialsContainer {
    private final Collection<GrantedAuthority> authorities;
    private Object details;
    private boolean authenticated = false;

    public AbstractAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null) {
            this.authorities = AuthorityUtils.NO_AUTHORITIES;
            return;
        }
        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull((Object)grantedAuthority, (String)"Authorities collection cannot contain any null elements");
        }
        this.authorities = Collections.unmodifiableList(new ArrayList<GrantedAuthority>(authorities));
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        if (this.getPrincipal() instanceof UserDetails) {
            return ((UserDetails)this.getPrincipal()).getUsername();
        }
        if (this.getPrincipal() instanceof AuthenticatedPrincipal) {
            return ((AuthenticatedPrincipal)this.getPrincipal()).getName();
        }
        if (this.getPrincipal() instanceof Principal) {
            return ((Principal)this.getPrincipal()).getName();
        }
        return this.getPrincipal() == null ? "" : this.getPrincipal().toString();
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public Object getDetails() {
        return this.details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    @Override
    public void eraseCredentials() {
        this.eraseSecret(this.getCredentials());
        this.eraseSecret(this.getPrincipal());
        this.eraseSecret(this.details);
    }

    private void eraseSecret(Object secret) {
        if (secret instanceof CredentialsContainer) {
            ((CredentialsContainer)secret).eraseCredentials();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractAuthenticationToken)) {
            return false;
        }
        AbstractAuthenticationToken test = (AbstractAuthenticationToken)obj;
        if (!this.authorities.equals(test.authorities)) {
            return false;
        }
        if (this.details == null && test.getDetails() != null) {
            return false;
        }
        if (this.details != null && test.getDetails() == null) {
            return false;
        }
        if (this.details != null && !this.details.equals(test.getDetails())) {
            return false;
        }
        if (this.getCredentials() == null && test.getCredentials() != null) {
            return false;
        }
        if (this.getCredentials() != null && !this.getCredentials().equals(test.getCredentials())) {
            return false;
        }
        if (this.getPrincipal() == null && test.getPrincipal() != null) {
            return false;
        }
        if (this.getPrincipal() != null && !this.getPrincipal().equals(test.getPrincipal())) {
            return false;
        }
        return this.isAuthenticated() == test.isAuthenticated();
    }

    @Override
    public int hashCode() {
        int code = 31;
        for (GrantedAuthority authority : this.authorities) {
            code ^= authority.hashCode();
        }
        if (this.getPrincipal() != null) {
            code ^= this.getPrincipal().hashCode();
        }
        if (this.getCredentials() != null) {
            code ^= this.getCredentials().hashCode();
        }
        if (this.getDetails() != null) {
            code ^= this.getDetails().hashCode();
        }
        if (this.isAuthenticated()) {
            code ^= 0xFFFFFFDB;
        }
        return code;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append(" [");
        sb.append("Principal=").append(this.getPrincipal()).append(", ");
        sb.append("Credentials=[PROTECTED], ");
        sb.append("Authenticated=").append(this.isAuthenticated()).append(", ");
        sb.append("Details=").append(this.getDetails()).append(", ");
        sb.append("Granted Authorities=").append(this.authorities);
        sb.append("]");
        return sb.toString();
    }
}

