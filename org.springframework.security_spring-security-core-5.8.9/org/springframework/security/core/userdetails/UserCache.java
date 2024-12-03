/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.userdetails;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserCache {
    public UserDetails getUserFromCache(String var1);

    public void putUserInCache(UserDetails var1);

    public void removeUserFromCache(String var1);
}

