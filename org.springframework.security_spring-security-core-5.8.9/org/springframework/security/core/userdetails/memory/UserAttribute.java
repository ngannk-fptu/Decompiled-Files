/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.userdetails.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserAttribute {
    private List<GrantedAuthority> authorities = new Vector<GrantedAuthority>();
    private String password;
    private boolean enabled = true;

    public void addAuthority(GrantedAuthority newAuthority) {
        this.authorities.add(newAuthority);
    }

    public List<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setAuthoritiesAsString(List<String> authoritiesAsStrings) {
        this.setAuthorities(new ArrayList<GrantedAuthority>(authoritiesAsStrings.size()));
        for (String authority : authoritiesAsStrings) {
            this.addAuthority(new SimpleGrantedAuthority(authority));
        }
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isValid() {
        return this.password != null && this.authorities.size() > 0;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

