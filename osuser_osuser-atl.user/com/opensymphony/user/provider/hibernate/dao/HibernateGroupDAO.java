/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.hibernate.dao;

import com.opensymphony.user.provider.hibernate.entity.HibernateGroup;
import java.util.List;

public interface HibernateGroupDAO {
    public int deleteGroupByGroupname(String var1);

    public HibernateGroup findGroupByGroupname(String var1);

    public List findGroups();

    public boolean saveGroup(HibernateGroup var1);
}

