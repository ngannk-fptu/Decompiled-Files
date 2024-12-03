/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.evermind.security.Group
 */
package com.opensymphony.user.provider.orion;

import com.opensymphony.user.Group;
import java.security.Permission;
import java.util.Iterator;
import java.util.Set;

public class OrionGroupAdapter
implements com.evermind.security.Group {
    private Group group;
    private Set permissions;

    public OrionGroupAdapter(Group group) {
        this.group = group;
    }

    public void setDescription(String s) {
        this.group.getPropertySet().setString("description", s);
    }

    public String getDescription() {
        return this.group.getPropertySet().getString("description");
    }

    public String getName() {
        return this.group.getName();
    }

    public void setPermissions(Set permissions) {
        this.permissions = permissions;
    }

    public Set getPermissions() throws UnsupportedOperationException {
        return this.permissions;
    }

    public void addPermission(Permission permission) throws UnsupportedOperationException {
        this.permissions.add(permission);
    }

    public boolean hasPermission(Permission permission) {
        if (this.permissions == null) {
            return false;
        }
        Iterator iterator = this.permissions.iterator();
        while (iterator.hasNext()) {
            if (!((Permission)iterator.next()).implies(permission)) continue;
            return true;
        }
        return false;
    }

    public void removePermission(Permission permission) throws UnsupportedOperationException {
        this.permissions.remove(permission);
    }
}

