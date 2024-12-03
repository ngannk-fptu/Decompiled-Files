/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.hibernate.entity;

import com.opensymphony.user.provider.hibernate.entity.HibernateUser;
import java.util.List;
import java.util.Set;

public interface HibernateGroup {
    public void setId(long var1);

    public long getId();

    public void setName(String var1);

    public String getName();

    public List getUserList();

    public List getUserNameList();

    public void setUsers(Set var1);

    public Set getUsers();

    public void addUser(HibernateUser var1);

    public boolean removeUser(HibernateUser var1);

    public boolean removeUser(String var1);
}

