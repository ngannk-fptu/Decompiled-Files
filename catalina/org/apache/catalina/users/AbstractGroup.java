/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.users;

import java.util.Iterator;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;

public abstract class AbstractGroup
implements Group {
    protected String description = null;
    protected String groupname = null;

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getGroupname() {
        return this.groupname;
    }

    @Override
    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    @Override
    public abstract Iterator<Role> getRoles();

    @Override
    public abstract UserDatabase getUserDatabase();

    @Override
    public abstract Iterator<User> getUsers();

    @Override
    public abstract void addRole(Role var1);

    @Override
    public abstract boolean isInRole(Role var1);

    @Override
    public abstract void removeRole(Role var1);

    @Override
    public abstract void removeRoles();

    @Override
    public String getName() {
        return this.getGroupname();
    }
}

