/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.users;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.UserDatabase;
import org.apache.catalina.users.AbstractUser;

public class GenericUser<UD extends UserDatabase>
extends AbstractUser {
    protected final UD database;
    protected final CopyOnWriteArrayList<Group> groups = new CopyOnWriteArrayList();
    protected final CopyOnWriteArrayList<Role> roles = new CopyOnWriteArrayList();

    GenericUser(UD database, String username, String password, String fullName, List<Group> groups, List<Role> roles) {
        this.database = database;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        if (groups != null) {
            this.groups.addAll(groups);
        }
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }

    @Override
    public Iterator<Group> getGroups() {
        return this.groups.iterator();
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
    public void addGroup(Group group) {
        if (this.groups.addIfAbsent(group)) {
            this.database.modifiedUser(this);
        }
    }

    @Override
    public void addRole(Role role) {
        if (this.roles.addIfAbsent(role)) {
            this.database.modifiedUser(this);
        }
    }

    @Override
    public boolean isInGroup(Group group) {
        return this.groups.contains(group);
    }

    @Override
    public boolean isInRole(Role role) {
        return this.roles.contains(role);
    }

    @Override
    public void removeGroup(Group group) {
        if (this.groups.remove(group)) {
            this.database.modifiedUser(this);
        }
    }

    @Override
    public void removeGroups() {
        if (!this.groups.isEmpty()) {
            this.groups.clear();
            this.database.modifiedUser(this);
        }
    }

    @Override
    public void removeRole(Role role) {
        if (this.roles.remove(role)) {
            this.database.modifiedUser(this);
        }
    }

    @Override
    public void removeRoles() {
        if (!this.roles.isEmpty()) {
            this.database.modifiedUser(this);
        }
        this.roles.clear();
    }

    @Override
    public void setFullName(String fullName) {
        this.database.modifiedUser(this);
        super.setFullName(fullName);
    }

    @Override
    public void setPassword(String password) {
        this.database.modifiedUser(this);
        super.setPassword(password);
    }

    @Override
    public void setUsername(String username) {
        this.database.modifiedUser(this);
        super.setUsername(username);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GenericUser) {
            GenericUser user = (GenericUser)obj;
            return user.database == this.database && this.username.equals(user.getUsername());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.database == null ? 0 : this.database.hashCode());
        result = 31 * result + (this.username == null ? 0 : this.username.hashCode());
        return result;
    }
}

