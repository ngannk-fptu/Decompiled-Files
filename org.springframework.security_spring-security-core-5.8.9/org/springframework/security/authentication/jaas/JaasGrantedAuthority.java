/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.authentication.jaas;

import java.security.Principal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public final class JaasGrantedAuthority
implements GrantedAuthority {
    private static final long serialVersionUID = 580L;
    private final String role;
    private final Principal principal;

    public JaasGrantedAuthority(String role, Principal principal) {
        Assert.notNull((Object)role, (String)"role cannot be null");
        Assert.notNull((Object)principal, (String)"principal cannot be null");
        this.role = role;
        this.principal = principal;
    }

    public Principal getPrincipal() {
        return this.principal;
    }

    @Override
    public String getAuthority() {
        return this.role;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof JaasGrantedAuthority) {
            JaasGrantedAuthority jga = (JaasGrantedAuthority)obj;
            return this.role.equals(jga.role) && this.principal.equals(jga.principal);
        }
        return false;
    }

    public int hashCode() {
        int result = this.principal.hashCode();
        result = 31 * result + this.role.hashCode();
        return result;
    }

    public String toString() {
        return "Jaas Authority [" + this.role + "," + this.principal + "]";
    }
}

