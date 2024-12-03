/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsService {
    public UserDetails loadUserByUsername(String var1) throws UsernameNotFoundException;
}

