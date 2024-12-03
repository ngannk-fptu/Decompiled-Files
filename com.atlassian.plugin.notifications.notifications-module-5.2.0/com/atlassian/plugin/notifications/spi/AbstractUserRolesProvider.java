/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.notifications.spi;

import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.plugin.notifications.spi.UserRolesProvider;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractUserRolesProvider
implements UserRolesProvider {
    private final Map<String, UserRole> roleMap = new HashMap<String, UserRole>();
    private final List<UserRole> roles = new LinkedList<UserRole>();

    protected void addRole(UserRole role) {
        this.roleMap.put(role.getID(), role);
        this.roles.add(role);
    }

    @Override
    public UserRole getRole(String id) {
        if (StringUtils.isBlank((CharSequence)id)) {
            return null;
        }
        return this.roleMap.get(id);
    }

    @Override
    public Iterable<UserRole> getRoles() {
        return this.roles;
    }
}

