/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.security.core.authority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public final class AuthorityUtils {
    public static final List<GrantedAuthority> NO_AUTHORITIES = Collections.emptyList();

    private AuthorityUtils() {
    }

    public static List<GrantedAuthority> commaSeparatedStringToAuthorityList(String authorityString) {
        return AuthorityUtils.createAuthorityList(StringUtils.tokenizeToStringArray((String)authorityString, (String)","));
    }

    public static Set<String> authorityListToSet(Collection<? extends GrantedAuthority> userAuthorities) {
        Assert.notNull(userAuthorities, (String)"userAuthorities cannot be null");
        HashSet<String> set = new HashSet<String>(userAuthorities.size());
        for (GrantedAuthority grantedAuthority : userAuthorities) {
            set.add(grantedAuthority.getAuthority());
        }
        return set;
    }

    public static List<GrantedAuthority> createAuthorityList(String ... authorities) {
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>(authorities.length);
        for (String authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }
        return grantedAuthorities;
    }
}

