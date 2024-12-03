/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.security.Principal;
import java.util.Iterator;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;

public interface Group
extends Principal {
    public String getDescription();

    public void setDescription(String var1);

    public String getGroupname();

    public void setGroupname(String var1);

    public Iterator<Role> getRoles();

    public UserDatabase getUserDatabase();

    public Iterator<User> getUsers();

    public void addRole(Role var1);

    public boolean isInRole(Role var1);

    public void removeRole(Role var1);

    public void removeRoles();
}

