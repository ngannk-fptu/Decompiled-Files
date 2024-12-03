/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.util.Iterator;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;

public interface UserDatabase {
    public Iterator<Group> getGroups();

    public String getId();

    public Iterator<Role> getRoles();

    public Iterator<User> getUsers();

    public void close() throws Exception;

    public Group createGroup(String var1, String var2);

    public Role createRole(String var1, String var2);

    public User createUser(String var1, String var2, String var3);

    public Group findGroup(String var1);

    public Role findRole(String var1);

    public User findUser(String var1);

    public void open() throws Exception;

    public void removeGroup(Group var1);

    public void removeRole(Role var1);

    public void removeUser(User var1);

    default public void modifiedGroup(Group group) {
    }

    default public void modifiedRole(Role role) {
    }

    default public void modifiedUser(User user) {
    }

    public void save() throws Exception;

    default public void backgroundProcess() {
    }

    default public boolean isAvailable() {
        return true;
    }

    default public boolean isSparse() {
        return false;
    }
}

