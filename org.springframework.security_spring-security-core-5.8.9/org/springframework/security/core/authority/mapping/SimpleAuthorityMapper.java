/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 */
package org.springframework.security.core.authority.mapping;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.util.Assert;

public final class SimpleAuthorityMapper
implements GrantedAuthoritiesMapper,
InitializingBean {
    private GrantedAuthority defaultAuthority;
    private String prefix = "ROLE_";
    private boolean convertToUpperCase = false;
    private boolean convertToLowerCase = false;

    public void afterPropertiesSet() {
        Assert.isTrue((!this.convertToUpperCase || !this.convertToLowerCase ? 1 : 0) != 0, (String)"Either convertToUpperCase or convertToLowerCase can be set to true, but not both");
    }

    public Set<GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        HashSet<GrantedAuthority> mapped = new HashSet<GrantedAuthority>(authorities.size());
        for (GrantedAuthority grantedAuthority : authorities) {
            mapped.add(this.mapAuthority(grantedAuthority.getAuthority()));
        }
        if (this.defaultAuthority != null) {
            mapped.add(this.defaultAuthority);
        }
        return mapped;
    }

    private GrantedAuthority mapAuthority(String name) {
        if (this.convertToUpperCase) {
            name = name.toUpperCase();
        } else if (this.convertToLowerCase) {
            name = name.toLowerCase();
        }
        if (this.prefix.length() > 0 && !name.startsWith(this.prefix)) {
            name = this.prefix + name;
        }
        return new SimpleGrantedAuthority(name);
    }

    public void setPrefix(String prefix) {
        Assert.notNull((Object)prefix, (String)"prefix cannot be null");
        this.prefix = prefix;
    }

    public void setConvertToUpperCase(boolean convertToUpperCase) {
        this.convertToUpperCase = convertToUpperCase;
    }

    public void setConvertToLowerCase(boolean convertToLowerCase) {
        this.convertToLowerCase = convertToLowerCase;
    }

    public void setDefaultAuthority(String authority) {
        Assert.hasText((String)authority, (String)"The authority name cannot be set to an empty value");
        this.defaultAuthority = new SimpleGrantedAuthority(authority);
    }
}

