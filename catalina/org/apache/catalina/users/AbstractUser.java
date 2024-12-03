/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.users;

import java.util.Iterator;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;

public abstract class AbstractUser
implements User {
    protected String fullName = null;
    protected String password = null;
    protected String username = null;

    @Override
    public String getFullName() {
        return this.fullName;
    }

    @Override
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public abstract Iterator<Group> getGroups();

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public abstract Iterator<Role> getRoles();

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public abstract void addGroup(Group var1);

    @Override
    public abstract void addRole(Role var1);

    @Override
    public abstract boolean isInGroup(Group var1);

    @Override
    public abstract boolean isInRole(Role var1);

    @Override
    public abstract void removeGroup(Group var1);

    @Override
    public abstract void removeGroups();

    @Override
    public abstract void removeRole(Role var1);

    @Override
    public abstract void removeRoles();

    @Override
    public String getName() {
        return this.getUsername();
    }
}

