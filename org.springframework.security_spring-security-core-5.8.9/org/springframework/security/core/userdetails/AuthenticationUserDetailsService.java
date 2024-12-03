/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.userdetails;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthenticationUserDetailsService<T extends Authentication> {
    public UserDetails loadUserDetails(T var1) throws UsernameNotFoundException;
}

