/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.security.Principal;
import java.util.Iterator;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.UserDatabase;

public interface User
extends Principal {
    public String getFullName();

    public void setFullName(String var1);

    public Iterator<Group> getGroups();

    public String getPassword();

    public void setPassword(String var1);

    public Iterator<Role> getRoles();

    public UserDatabase getUserDatabase();

    public String getUsername();

    public void setUsername(String var1);

    public void addGroup(Group var1);

    public void addRole(Role var1);

    public boolean isInGroup(Group var1);

    public boolean isInRole(Role var1);

    public void removeGroup(Group var1);

    public void removeGroups();

    public void removeRole(Role var1);

    public void removeRoles();
}

