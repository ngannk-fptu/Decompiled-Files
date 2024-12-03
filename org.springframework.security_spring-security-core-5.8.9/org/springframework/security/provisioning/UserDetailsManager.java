/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.provisioning;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserDetailsManager
extends UserDetailsService {
    public void createUser(UserDetails var1);

    public void updateUser(UserDetails var1);

    public void deleteUser(String var1);

    public void changePassword(String var1, String var2);

    public boolean userExists(String var1);
}

