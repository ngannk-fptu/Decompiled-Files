/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.hibernate.entity;

import com.opensymphony.user.provider.hibernate.entity.HibernateGroup;
import java.util.List;
import java.util.Set;

public interface HibernateUser {
    public void setId(long var1);

    public long getId();

    public void setName(String var1);

    public String getName();

    public List getGroupList();

    public List getGroupNameList();

    public void setGroups(Set var1);

    public Set getGroups();

    public void setPassword(String var1);

    public void setPasswordHash(String var1);

    public String getPasswordHash();

    public void addGroup(HibernateGroup var1);

    public boolean authenticate(String var1);

    public void removeGroup(HibernateGroup var1);
}

