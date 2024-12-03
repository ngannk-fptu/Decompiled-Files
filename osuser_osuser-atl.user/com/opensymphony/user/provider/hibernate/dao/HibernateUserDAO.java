/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.hibernate.dao;

import com.opensymphony.user.provider.hibernate.entity.HibernateUser;
import java.util.List;

public interface HibernateUserDAO {
    public int deleteUserByUsername(String var1);

    public HibernateUser findUserByUsername(String var1);

    public HibernateUser findUserByUsernameAndGroupname(String var1, String var2);

    public List findUsers();

    public boolean saveUser(HibernateUser var1);

    public boolean updateUser(HibernateUser var1);
}

