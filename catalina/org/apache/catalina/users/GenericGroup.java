/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.users;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.catalina.users.AbstractGroup;

public class GenericGroup<UD extends UserDatabase>
extends AbstractGroup {
    protected final UD database;
    protected final CopyOnWriteArrayList<Role> roles = new CopyOnWriteArrayList();

    GenericGroup(UD database, String groupname, String description, List<Role> roles) {
        this.database = database;
        this.groupname = groupname;
        this.description = description;
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }

    @Override
    public Iterator<Role> getRoles() {
        return this.roles.iterator();
    }

    @Override
    public UserDatabase getUserDatabase() {
        return this.database;
    }

    @Override
    public Iterator<User> getUsers() {
        ArrayList<User> results = new ArrayList<User>();
        Iterator<User> users = this.database.getUsers();
        while (users.hasNext()) {
            User user = users.next();
            if (!user.isInGroup(this)) continue;
            results.add(user);
        }
        return results.iterator();
    }

    @Override
    public void addRole(Role role) {
        if (this.roles.addIfAbsent(role)) {
            this.database.modifiedGroup(this);
        }
    }

    @Override
    public boolean isInRole(Role role) {
        return this.roles.contains(role);
    }

    @Override
    public void removeRole(Role role) {
        if (this.roles.remove(role)) {
            this.database.modifiedGroup(this);
        }
    }

    @Override
    public void removeRoles() {
        if (!this.roles.isEmpty()) {
            this.roles.clear();
            this.database.modifiedGroup(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GenericGroup) {
            GenericGroup group = (GenericGroup)obj;
            return group.database == this.database && this.groupname.equals(group.getGroupname());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.database == null ? 0 : this.database.hashCode());
        result = 31 * result + (this.groupname == null ? 0 : this.groupname.hashCode());
        return result;
    }
}

