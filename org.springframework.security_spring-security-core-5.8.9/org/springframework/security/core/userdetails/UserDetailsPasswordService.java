/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.userdetails;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsPasswordService {
    public UserDetails updatePassword(UserDetails var1, String var2);
}

