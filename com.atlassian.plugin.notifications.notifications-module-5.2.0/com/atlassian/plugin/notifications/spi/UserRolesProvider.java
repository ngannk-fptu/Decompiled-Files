/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.spi;

import com.atlassian.plugin.notifications.spi.UserRole;

public interface UserRolesProvider {
    public UserRole getRole(String var1);

    public Iterable<UserRole> getRoles();
}

